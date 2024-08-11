package com.zosh.ecommerce.serviceImpl;

//import com.zosh.ecommerce.entities.User;
import com.zosh.ecommerce.entities.User;
import com.zosh.ecommerce.entities.UserUserDetail;
import com.zosh.ecommerce.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByEmail(username)
                .map(UserUserDetail::new).orElseThrow(() -> {
                    System.out.println("User not found for email:" + username);
                    return new UsernameNotFoundException("No user found");
                });
    }
}
