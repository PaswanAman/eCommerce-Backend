package com.zosh.ecommerce.service;


import com.zosh.ecommerce.Dto.AdminDto;
import org.springframework.stereotype.Service;

@Service
public interface AdminService {
    AdminDto registerAdmin(AdminDto adminDto);
}
