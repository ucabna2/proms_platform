package com.noesisinformatica.northumbriaproms.service;

/*-
 * #%L
 * Proms Platform
 * %%
 * Copyright (C) 2017 - 2018 Termlex
 * %%
 * This software is Copyright and Intellectual Property of Termlex Inc Limited.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation as version 3 of the
 * License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 * #L%
 */

import com.noesisinformatica.northumbriaproms.domain.Questionnaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing Questionnaire.
 */
public interface QuestionnaireService {

    /**
     * Save a questionnaire.
     *
     * @param questionnaire the entity to save
     * @return the persisted entity
     */
    Questionnaire save(Questionnaire questionnaire);

    /**
     * Get all the questionnaires.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Questionnaire> findAll(Pageable pageable);

    /**
     * Get the "id" questionnaire.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Questionnaire findOne(Long id);

    /**
     * Delete the "id" questionnaire.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the questionnaire corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Questionnaire> search(String query, Pageable pageable);
}
