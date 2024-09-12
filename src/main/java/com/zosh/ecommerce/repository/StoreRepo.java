package com.zosh.ecommerce.repository;

import com.zosh.ecommerce.entities.Store;
import com.zosh.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepo extends JpaRepository<Store, Long> {
    Optional<Store> findBySeller(User seller);
}
