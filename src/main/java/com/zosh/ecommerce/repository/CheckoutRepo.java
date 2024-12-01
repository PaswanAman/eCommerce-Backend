package com.zosh.ecommerce.repository;

import com.zosh.ecommerce.entities.CheckOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckoutRepo extends JpaRepository<CheckOut,Long> {

}
