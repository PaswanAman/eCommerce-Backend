package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.Dto.AdminDto;
import com.zosh.ecommerce.Dto.UserDto;
import com.zosh.ecommerce.config.AppConstants;
import com.zosh.ecommerce.entities.Admin;
import com.zosh.ecommerce.entities.Role;
import com.zosh.ecommerce.entities.User;
import com.zosh.ecommerce.repository.AdminRepo;
import com.zosh.ecommerce.repository.RoleRepo;
import com.zosh.ecommerce.service.AdminService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @Override
    public AdminDto registerAdmin(AdminDto adminDto) {
        Admin admin = this.modelMapper.map(adminDto, Admin.class);
        admin.setPassword(this.passwordEncoder.encode(admin.getPassword()));

        Role role = this.roleRepo.findById(AppConstants.ROLE_ADMIN).get();
        admin.getRoles().add(role);

        Admin newAdmin = this.adminRepo.save(admin);
        return this.modelMapper.map(newAdmin, AdminDto.class);
    }
}
