package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.entities.Admin;
import com.zosh.ecommerce.entities.AdminUserDetail;
import com.zosh.ecommerce.repository.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailService implements UserDetailsService {
   @Autowired
   private AdminRepo adminRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
         return adminRepo.findByEmail(username)
                 .map(AdminUserDetail::new).orElseThrow(() ->{
                     System.out.println("User not found for email:" + username);
                     return new UsernameNotFoundException("No user found");
                 });
    }
}
