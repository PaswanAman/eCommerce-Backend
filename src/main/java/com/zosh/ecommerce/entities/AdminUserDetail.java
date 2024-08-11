package com.zosh.ecommerce.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AdminUserDetail implements UserDetails {
    private String userName;
    private String password;
    private Set<Role> role;

    public AdminUserDetail(){

    }

    public AdminUserDetail(Admin admin){
         userName = admin.getEmail();
         password = admin.getPassword();
         role = admin.getRoles();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<SimpleGrantedAuthority> authorities = this.role.stream()
                .map((role) -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
        return authorities;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }
}
