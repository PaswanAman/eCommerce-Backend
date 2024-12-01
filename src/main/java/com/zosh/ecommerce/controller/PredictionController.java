package com.zosh.ecommerce.controller;

import com.zosh.ecommerce.Dto.CategoryDto;
import com.zosh.ecommerce.entities.Predictions;
import com.zosh.ecommerce.serviceImpl.PredictionsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/prediction")
public class PredictionController {
    @Autowired
    private PredictionsServiceImpl predictionsService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllPredictions() {
        List<Predictions> predictions = predictionsService.findAllPredictions();
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("status","success","predictions", predictions));

    }
}
