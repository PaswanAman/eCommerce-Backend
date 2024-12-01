package com.zosh.ecommerce.repository;

import com.zosh.ecommerce.entities.Predictions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PredictionRepo extends JpaRepository<Predictions, Integer> {
    Predictions findByFilePath(String filePath);

    boolean existsByPersonId(int personId);
    
    void deleteById(int id);

    List<Predictions> findByPersonId(int personId);
}
