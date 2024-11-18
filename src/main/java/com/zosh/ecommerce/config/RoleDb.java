package com.zosh.ecommerce.config;

import com.zosh.ecommerce.entities.Role;
import com.zosh.ecommerce.repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RoleDb implements CommandLineRunner {
    @Autowired
    private RoleRepo roleRepo;
    @Override
    public void run(String... args) throws Exception {
        roleRepo.saveAll(Arrays.asList(
                new Role(1,"ROLE_ADMIN"),
                new Role(2, "ROLE_BUYER"),
                new Role(3,"ROLE_SELLER")
        ));
    }
}
