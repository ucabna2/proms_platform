package com.noesisinformatica.northumbriaproms;

import com.noesisinformatica.northumbriaproms.config.ApplicationProperties;
import com.noesisinformatica.northumbriaproms.config.Constants;
import com.noesisinformatica.northumbriaproms.config.DefaultProfileUtil;
import com.noesisinformatica.northumbriaproms.domain.*;
import com.noesisinformatica.northumbriaproms.domain.enumeration.GenderType;
import com.noesisinformatica.northumbriaproms.repository.*;
import com.noesisinformatica.northumbriaproms.repository.search.UserSearchRepository;
import com.noesisinformatica.northumbriaproms.security.AuthoritiesConstants;
import com.noesisinformatica.northumbriaproms.service.PatientService;
import com.noesisinformatica.northumbriaproms.service.ProcedureService;
import com.noesisinformatica.northumbriaproms.service.QuestionnaireService;
import com.noesisinformatica.northumbriaproms.service.util.RandomUtil;
import com.opencsv.CSVReader;
import io.github.jhipster.config.JHipsterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;

@ComponentScan
@EnableAutoConfiguration(exclude = {MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class})
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
public class NorthumbriapromsApp {

    private static final Logger log = LoggerFactory.getLogger(NorthumbriapromsApp.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Environment env;

    public NorthumbriapromsApp(Environment env) {
        this.env = env;
    }

    /**
     * Initializes northumbriaproms.
     * <p>
     * Spring profiles can be configured with a program arguments --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="http://www.jhipster.tech/profiles/">http://www.jhipster.tech/profiles/</a>.
     */
    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run " +
                "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not " +
                "run with both the 'dev' and 'cloud' profiles at the same time.");
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     * @throws UnknownHostException if the local host name could not be resolved into an address
     */
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(NorthumbriapromsApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        ConfigurableApplicationContext ctx = app.run(args);
        Environment env = ctx.getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}\n\t" +
                "External: \t{}://{}:{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            env.getProperty("server.port"),
            protocol,
            InetAddress.getLocalHost().getHostAddress(),
            env.getProperty("server.port"),
            env.getActiveProfiles());

        if (Arrays.asList(env.getActiveProfiles()).contains("dev") || Arrays.asList(env.getActiveProfiles()).contains("prod")) {

            // add data if none exists
            verifyAndImportProcedures(ctx);
            verifyAndImportQuestionnaires(ctx);
            verifyAndImportPatients(ctx);
            verifyAndImportConsultants(ctx);

            // clear security context
            log.info("Logging out admin user after importing entities");
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Utility bootstrap method that imports procedures if none is found.
     * @param ctx the application context
     */
    private static void verifyAndImportProcedures(ConfigurableApplicationContext ctx) {

        ProcedureService procedureService = ctx.getBean(ProcedureService.class);
        long count = ctx.getBean(ProcedureRepository.class).count();
        log.info("No of existing procedures {}", count);

        if (count == 0) {

            try (InputStream inputStream = NorthumbriapromsApp.class.getClassLoader().getResourceAsStream("config/procedures.csv")){
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                int counter = 0;
                String line = bufReader.readLine();
                while(line != null){
                    if(counter > 0){
                        Procedure procedure = new Procedure();
                        String[] parts = line.split(",");
                        // create procedure form parts
                        procedure.setLocalCode(Integer.valueOf(parts[0]));
                        procedure.setName(parts[1]);
                        // save procedure
                        procedureService.save(procedure);
                    }
                    counter++;
                    line = bufReader.readLine();
                }
            } catch (IOException e){
                log.error("Unable to read procedures.csv from class path. Nested exception is : ", e);
            }
        }
    }

    /**
     * Utility bootstrap method that imports questionnaires if none found.
     * @param ctx the application context
     */
    private static void verifyAndImportQuestionnaires(ConfigurableApplicationContext ctx) {

        QuestionnaireService questionnaireService = ctx.getBean(QuestionnaireService.class);
        long count = ctx.getBean(QuestionnaireRepository.class).count();
        log.info("No of existing questionnaires {} " , count);

        if (count == 0) {

            try (InputStream inputStream = NorthumbriapromsApp.class.getClassLoader().getResourceAsStream("config/questionnaires.csv")){
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                int counter = 0;
                String line = bufReader.readLine();
                while(line != null){
                    if(counter > 0){
                        Questionnaire questionnaire = new Questionnaire();
                        String[] parts = line.split(",");
                        // create questionnaire form parts
                        questionnaire.setName(parts[0]);
                        // save questionnaire
                        questionnaireService.save(questionnaire);
                    }
                    counter++;
                    line = bufReader.readLine();
                }
            } catch (IOException e){
                log.error("Unable to read questionnaires.csv from class path. Nested exception is : ", e);
            }
        }
    }

    /**
     * Utility bootstrap method that imports patients if none found.
     * @param ctx the application context
     */
    private static void verifyAndImportPatients(ConfigurableApplicationContext ctx) {

        PatientService patientService = ctx.getBean(PatientService.class);
        long count = ctx.getBean(PatientRepository.class).count();
        log.info("No of existing patients {} " , count);

        if (count == 0) {

            try (InputStream inputStream = NorthumbriapromsApp.class.getClassLoader().getResourceAsStream("config/patients.csv")){
                CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                reader.forEach(parts -> {
                    log.debug("parts = {}", parts);
                    if(! parts[0].startsWith("#")){
                        Patient patient = new Patient();
                        // create patient form parts
                        patient.setGivenName(parts[2]);
                        patient.setFamilyName(parts[3]);

                        Address address = new Address(parts[4]);
                        address.addLine(parts[5]);
                        address.setCity(parts[6]);
                        patient.addAddress(address);

                        address.setPostalCode(parts[7]);
                        LocalDate date = LocalDate.parse(parts[9], formatter);
                        patient.setBirthDate(date.atStartOfDay(ZoneOffset.UTC));
                        patient.setGender(GenderType.valueOf(parts[10]));
                        patient.setNhsNumber(Long.valueOf(parts[11]));
                        // save questionnaire
                        patientService.save(patient);
                    }
                });
            } catch (IOException e){
                log.error("Unable to read questionnaires.csv from class path. Nested exception is : ", e);
            }
        }
    }

    /**
     * Utility bootstrap method that imports consultants if none found.
     * @param ctx the application context
     */
    private static void verifyAndImportConsultants(ConfigurableApplicationContext ctx) {

        UserRepository userRepository = ctx.getBean(UserRepository.class);
        PasswordEncoder passwordEncoder = ctx.getBean(PasswordEncoder.class);
        UserSearchRepository userSearchRepository = ctx.getBean(UserSearchRepository.class);
        long count = userRepository.findAllByAuthoritiesName(AuthoritiesConstants.CONSULTANT, null).getTotalElements();
        log.info("No of existing consultants {} ", count);

        if (count == 0) {

            AuthorityRepository authorityRepository = ctx.getBean(AuthorityRepository.class);
            // find consultant role
            Authority consultantRole = authorityRepository.findOne(AuthoritiesConstants.CONSULTANT);
            try (InputStream inputStream = NorthumbriapromsApp.class.getClassLoader().getResourceAsStream("config/consultants.csv")){
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                int counter = 0;
                String line = bufReader.readLine();
                while(line != null){
                    if(counter > 0){
                        String[] parts = line.split(",");
                        // create consultant from part
                        User user = new User();
                        user.setTitle(parts[0]);
                        user.setFirstName(parts[1]);
                        user.setLastName(parts[2]);
                        user.setLogin(parts[3]);
                        user.setEmail(parts[4] + "@promsapp.com");
                        user.addAuthority(consultantRole);
                        if (user.getLangKey() == null) {
                            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
                        } else {
                            user.setLangKey(user.getLangKey());
                        }

                        // set random password if no password set
                        if (user.getPassword() == null) {
                            user.setPassword(RandomUtil.generatePassword());
                        }

                        String encryptedPassword = passwordEncoder.encode(user.getPassword());
                        user.setPassword(encryptedPassword);
                        user.setResetKey(RandomUtil.generateResetKey());
                        user.setResetDate(Instant.now());
                        user.setActivated(true);
                        log.debug("Saving user = {}", user);
                        userRepository.save(user);
                        userSearchRepository.save(user);
                    }
                    counter++;
                    line = bufReader.readLine();
                }
            } catch (IOException e){
                log.error("Unable to read consultants.csv from class path. Nested exception is : ", e);
            }
        }
    }

}
