package com.zosh.ecommerce.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDto {
    private Long id;
    private Long commentId; // ID of the parent comment
    private Long userId;
    private String reply;
    private LocalDateTime createdAt;
}
