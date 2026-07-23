package com.eiris.backend.repository;

import com.eiris.backend.entity.AgencySale;
import com.eiris.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AgencySaleRepository extends JpaRepository<AgencySale, UUID> {
    List<AgencySale> findByAgencyUserOrderByCreatedAtDesc(User agencyUser);
}
