package com.zosh.ecommerce.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long productId;
    private String title;
    private String brand;
    private String description;
    private Double price;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime expirationDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime addedDate;
    private Boolean historyStatus;
    private Boolean hiddenPost;
    private boolean sold;
    private String categoryName;
    private List<String> imageUrls;
    private Long sellerId;

//    public void setImageUrls(List<String> imageUrls) {
//    }
}
