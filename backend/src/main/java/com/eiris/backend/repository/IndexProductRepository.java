package com.eiris.backend.repository;

import com.eiris.backend.entity.IndexProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IndexProductRepository extends JpaRepository<IndexProduct, UUID> {
    List<IndexProduct> findByCategoryOrderByCreatedAtDesc(String category);

    // Fetch the latest product for each category using a native query
    @Query(value = "SELECT DISTINCT ON (category) * FROM index_products ORDER BY category, created_at DESC", nativeQuery = true)
    List<IndexProduct> findLatestProductsPerCategory();
}
