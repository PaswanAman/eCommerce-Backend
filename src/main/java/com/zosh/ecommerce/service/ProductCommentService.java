package com.zosh.ecommerce.service;

import com.zosh.ecommerce.Dto.ProductCommentDto;
import org.springframework.stereotype.Service;

@Service
public interface ProductCommentService {
    ProductCommentDto addComment(String userToken, Long productId,ProductCommentDto productCommentDto);
}
