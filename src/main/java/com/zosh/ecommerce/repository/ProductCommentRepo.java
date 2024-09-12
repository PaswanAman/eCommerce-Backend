package com.zosh.ecommerce.repository;

import com.zosh.ecommerce.entities.ProductComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCommentRepo extends JpaRepository<ProductComment,Long> {
//    List<ProductComment> findByProductId(Long productId);
}
