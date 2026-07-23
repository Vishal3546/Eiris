package com.eiris.backend.repository;

import com.eiris.backend.entity.AgencyClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AgencyClientRepository extends JpaRepository<AgencyClient, UUID> {
    List<AgencyClient> findByAgencyId(UUID agencyId);
}
