package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.Dto.ProductCommentDto;
import com.zosh.ecommerce.config.JwtTokenHelper;
import com.zosh.ecommerce.entities.Product;
import com.zosh.ecommerce.entities.ProductComment;
import com.zosh.ecommerce.entities.User;
import com.zosh.ecommerce.exception.ResourceNotFoundException;
import com.zosh.ecommerce.repository.CommentReplyRepo;
import com.zosh.ecommerce.repository.ProductCommentRepo;
import com.zosh.ecommerce.repository.ProductRepo;
import com.zosh.ecommerce.repository.UserRepo;
import com.zosh.ecommerce.service.ProductCommentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProductCommentServiceImpl implements ProductCommentService {
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ProductCommentRepo productCommentRepo;
    @Autowired
    private CommentReplyRepo commentReplyRepo;
    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Autowired
    private ModelMapper modelMapper;


    @Override
    public ProductCommentDto addComment(String userToken, Long productId,ProductCommentDto productCommentDto) {
        try {
            String token = userToken.replaceAll("Bearer", " ").trim();
            String username = jwtTokenHelper.getUsernameFromToken(token);
            User user = this.userRepo.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("Seller", "email", username));

            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "Id", productId));
            ProductComment comment = new ProductComment();
            comment.setProduct(product);
            comment.setUser(user);
            comment.setComment(comment.getComment());
            comment.setCreatedAt(LocalDateTime.now());

            ProductComment savedComment = productCommentRepo.save(comment);
            return this.modelMapper.map(savedComment, ProductCommentDto.class);
        }catch (Exception e) {

            throw new RuntimeException("Failed to add rating", e);
        }
    }
}
