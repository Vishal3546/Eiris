package com.eiris.backend.repository;

import com.eiris.backend.entity.AgencyOrder;
import com.eiris.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AgencyOrderRepository extends JpaRepository<AgencyOrder, UUID> {
    List<AgencyOrder> findByAgencyUserOrderByCreatedAtDesc(User agencyUser);
}
