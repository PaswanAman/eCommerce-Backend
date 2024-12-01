package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.Dto.UserDto;
import com.zosh.ecommerce.entities.Predictions;
import com.zosh.ecommerce.repository.PredictionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class PredictionsServiceImpl {
    @Autowired
    private PredictionRepo predictionRepo;
    public List<Predictions> findAllPredictions() {
        return predictionRepo.findAll();
    }
}
