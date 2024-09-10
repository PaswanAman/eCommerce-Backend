package com.zosh.ecommerce.Dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreDto {
    private Long id;
    private String storeName;
    private List<MultipartFile> storeImage;
    private List<String> storeImageUrls;
    private List<String> storeImageName;
    private String panNumber;
    private String bankName;
    private String accountNumber;
    private String branchName;
    private String storeAddress;
    private String latitude;
    private String longitude;
}
