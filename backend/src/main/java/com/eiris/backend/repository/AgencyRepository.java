package com.eiris.backend.repository;

import com.eiris.backend.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, UUID> {
}
