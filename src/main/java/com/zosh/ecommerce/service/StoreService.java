package com.zosh.ecommerce.service;

import com.zosh.ecommerce.Dto.StoreDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface StoreService {
    StoreDto createStore(StoreDto storeDto, String userToken, List<MultipartFile> image) throws IOException;
    StoreDto storeGetById(Long storeId);
    List<StoreDto> getAllStores();
    StoreDto getStoreBySellerId(Long sellerId);
}
