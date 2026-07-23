package com.eiris.backend.repository;

import com.eiris.backend.entity.AgencyInventory;
import com.eiris.backend.entity.Product;
import com.eiris.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgencyInventoryRepository extends JpaRepository<AgencyInventory, UUID> {
    List<AgencyInventory> findByAgencyUser(User agencyUser);
    Optional<AgencyInventory> findByAgencyUserAndProduct(User agencyUser, Product product);
}
