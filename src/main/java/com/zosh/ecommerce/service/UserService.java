package com.zosh.ecommerce.service;

import com.zosh.ecommerce.Dto.SellerDto;
import com.zosh.ecommerce.Dto.UserDto;
import com.zosh.ecommerce.Dto.UserUpdateDto;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface UserService {
    UserDto registerNewBuyer(UserDto userDto);
    SellerDto registerNewSeller(SellerDto sellerDto);
    UserDto updateUser(UserUpdateDto userUpdateDto, Long userId);
    UserDto getUserByUserName(String userToken);
    Map<String, Object> getAllUsers(Integer pageNumber, Integer pageSize);

    boolean existByMobileNumber(String mobileNumber);
    boolean existByEmail(String email);
    public UserDto changePassword(UserDto userDto, String userToken);
    boolean verifyChangePassword(String userToken, String oldPassword);
    public UserDto forgetPassword(UserDto userDto, String userToken);
    public boolean verifyForgetPassword(String userToken, String oldPassword);


}
