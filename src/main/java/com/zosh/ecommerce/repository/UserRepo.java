package com.zosh.ecommerce.repository;

import com.zosh.ecommerce.Dto.UserDto;
import com.zosh.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
//    Optional<User> findByMobileNumber(String mobileNumber);
    Optional<User> findByEmail(String email);
//    Optional<Employee> findByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
    boolean existsByEmail(String email);
//    Optional<User> findByName(String fileName);
}
