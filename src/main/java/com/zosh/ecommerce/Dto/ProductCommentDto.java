package com.zosh.ecommerce.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCommentDto {
    private Long id;
    private Long productId;
    private Long userId;
    private String comment;
    private LocalDateTime createdAt;
    private List<ReplyDto> replies;
}
