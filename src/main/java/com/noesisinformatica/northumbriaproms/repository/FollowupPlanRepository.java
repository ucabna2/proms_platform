package com.noesisinformatica.northumbriaproms.repository;

import com.noesisinformatica.northumbriaproms.domain.FollowupPlan;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.Optional;


/**
 * Spring Data JPA repository for the FollowupPlan entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FollowupPlanRepository extends JpaRepository<FollowupPlan, Long> {

    Optional<FollowupPlan> findOneByProcedureBookingId(Long id);
}