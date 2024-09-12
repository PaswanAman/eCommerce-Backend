package com.zosh.ecommerce.repository;

import com.zosh.ecommerce.entities.CommentReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentReplyRepo extends JpaRepository<CommentReply,Long> {
//    List<CommentReply> findByProductCommentId(Long productCommentId);
}
