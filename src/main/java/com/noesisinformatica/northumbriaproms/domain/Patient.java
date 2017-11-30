package com.noesisinformatica.northumbriaproms.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noesisinformatica.northumbriaproms.domain.enumeration.GenderType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * A Patient.
 */
@Entity
@Table(name = "patient")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "patient")
public class Patient implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "family_name", nullable = false)
    private String familyName;

    @NotNull
    @Column(name = "given_name", nullable = false)
    private String givenName;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private ZonedDateTime birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private GenderType gender;

    @Column(name = "nhs_number")
    private Long nhsNumber;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "patient", cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Address> addresses = new HashSet<>();

    @OneToMany(mappedBy = "patient", cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ProcedureBooking> procedureBookings = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFamilyName() {
        return familyName;
    }

    public Patient familyName(String familyName) {
        this.familyName = familyName;
        return this;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public Patient givenName(String givenName) {
        this.givenName = givenName;
        return this;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public ZonedDateTime getBirthDate() {
        return birthDate;
    }

    public Patient birthDate(ZonedDateTime birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public void setBirthDate(ZonedDateTime birthDate) {
        this.birthDate = birthDate;
    }

//    public void setBirthDate(String dob) {
//        LocalDate date = LocalDate.parse(dob, formatter);
//        this.birthDate = date.atStartOfDay(ZoneOffset.UTC);
//    }

    public GenderType getGender() {
        return gender;
    }

    public Patient gender(GenderType gender) {
        this.gender = gender;
        return this;
    }

    public void setGender(GenderType gender) {
        this.gender = gender;
    }

    public Long getNhsNumber() {
        return nhsNumber;
    }

    public Patient nhsNumber(Long nhsNumber) {
        this.nhsNumber = nhsNumber;
        return this;
    }

    public void setNhsNumber(Long nhsNumber) {
        this.nhsNumber = nhsNumber;
    }

    public String getEmail() {
        return email;
    }

    public Patient email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public Patient addresses(Set<Address> addresses) {
        this.addresses = addresses;
        return this;
    }

    public Patient addAddress(Address address) {
        this.addresses.add(address);
        address.setPatient(this);
        return this;
    }

    public Patient removeAddress(Address address) {
        this.addresses.remove(address);
        address.setPatient(null);
        return this;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    public Set<ProcedureBooking> getProcedureBookings() {
        return procedureBookings;
    }

    public Patient procedureBookings(Set<ProcedureBooking> procedureBookings) {
        this.procedureBookings = procedureBookings;
        return this;
    }

    public Patient addProcedureBookings(ProcedureBooking procedureBooking) {
        this.procedureBookings.add(procedureBooking);
        procedureBooking.setPatient(this);
        return this;
    }

    public Patient removeProcedureBookings(ProcedureBooking procedureBooking) {
        this.procedureBookings.remove(procedureBooking);
        procedureBooking.setPatient(null);
        return this;
    }

    public void setProcedureBookings(Set<ProcedureBooking> procedureBookings) {
        this.procedureBookings = procedureBookings;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Patient patient = (Patient) o;
        if (patient.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), patient.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Patient{" +
            "id=" + getId() +
            ", familyName='" + getFamilyName() + "'" +
            ", givenName='" + getGivenName() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", gender='" + getGender() + "'" +
            ", nhsNumber=" + getNhsNumber() +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
