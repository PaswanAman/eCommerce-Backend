package com.zosh.ecommerce.repository;

import com.zosh.ecommerce.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Integer> {
//    Role findByName(String name);
}
