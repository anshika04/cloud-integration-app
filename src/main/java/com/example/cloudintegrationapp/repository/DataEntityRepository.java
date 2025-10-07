package com.example.cloudintegrationapp.repository;

import com.example.cloudintegrationapp.model.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataEntityRepository extends JpaRepository<DataEntity, Long> {
    
    /**
     * Find data entity by reference ID
     */
    Optional<DataEntity> findByReferenceId(String referenceId);
    
    /**
     * Check if data entity exists by reference ID
     */
    boolean existsByReferenceId(String referenceId);
    
    /**
     * Find data entities by category
     */
    List<DataEntity> findByCategory(String category);
    
    /**
     * Find data entities by status
     */
    List<DataEntity> findByStatus(String status);
    
    /**
     * Find data entities by category and status
     */
    List<DataEntity> findByCategoryAndStatus(String category, String status);
    
    /**
     * Find data entities by name containing (case insensitive)
     */
    List<DataEntity> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find data entities by description containing (case insensitive)
     */
    List<DataEntity> findByDescriptionContainingIgnoreCase(String description);
    
    /**
     * Custom query to find entities by custom criteria
     */
    @Query("SELECT d FROM DataEntity d WHERE " +
           "(:category IS NULL OR d.category = :category) AND " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<DataEntity> findWithCustomCriteria(@Param("category") String category, 
                                           @Param("status") String status, 
                                           @Param("name") String name);
    
    /**
     * Count entities by category
     */
    long countByCategory(String category);
    
    /**
     * Count entities by status
     */
    long countByStatus(String status);
    
    /**
     * Delete entity by reference ID
     */
    void deleteByReferenceId(String referenceId);
}
