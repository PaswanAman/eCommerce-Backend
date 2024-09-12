package com.zosh.ecommerce.controller;

import com.zosh.ecommerce.Dto.StoreDto;
import com.zosh.ecommerce.exception.ResourceNotFoundException;
import com.zosh.ecommerce.exception.StoreCreationException;
import com.zosh.ecommerce.service.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class StoreController {
    @Autowired
    private StoreService storeService;

    private static final Logger logger = LoggerFactory.getLogger(StoreController.class);

    @PostMapping("/create")
    public ResponseEntity<?> createStore(@ModelAttribute StoreDto storeDto, @RequestHeader(value = "Authorization") String userToken,@RequestParam("images") List<MultipartFile> images) throws IOException {
        try {
            StoreDto createdStore = storeService.createStore(storeDto, userToken,images);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status","success","message","Store created successfully",
                    "store", createdStore));
        } catch (DataIntegrityViolationException e) {
            throw new StoreCreationException("A store with the same name or PAN number already exists.");
        }
        catch (IllegalArgumentException e) {
            logger.error("Image support only");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("status", "error", "message", e.getMessage()));

        } catch (Exception e) {
            logger.error("Failed to create Store");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "error", "message", "Failed to create store"));
        }

    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<?> getStoreById(@PathVariable Long storeId) {
        logger.info("get store by id api called");
        try {
            StoreDto storeDto = storeService.storeGetById(storeId);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", "success","message",storeDto));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + ex.getMessage());
        }
    }

    @GetMapping("/store/all")
    public ResponseEntity<?> getAllStores() {
        try {
            List<StoreDto> storeDtos = storeService.getAllStores();
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("status","success","message",storeDtos));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + ex.getMessage());
        }
    }

    @GetMapping("/store/seller/{sellerId}")
    public ResponseEntity<?> getStoreBySellerId(@PathVariable Long sellerId) {
        try {
            StoreDto storeDto = storeService.getStoreBySellerId(sellerId);
            return ResponseEntity.ok(storeDto);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + ex.getMessage());
        }
    }
}
