package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.Dto.SellerDto;
import com.zosh.ecommerce.Dto.UserDto;
import com.zosh.ecommerce.Dto.UserUpdateDto;
import com.zosh.ecommerce.config.AppConstants;
import com.zosh.ecommerce.config.JwtTokenHelper;
import com.zosh.ecommerce.entities.Cart;
import com.zosh.ecommerce.entities.Role;
import com.zosh.ecommerce.entities.User;
import com.zosh.ecommerce.repository.RoleRepo;
import com.zosh.ecommerce.repository.UserRepo;
import com.zosh.ecommerce.service.FileService;
import com.zosh.ecommerce.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileService fileService;

    @Autowired
    private OtpService otpService;

    @Value("${picture.base-url}")
    private String baseurl;



    @Override
    public UserDto registerNewBuyer(UserDto userDto){
        User user = this.modelMapper.map(userDto, User.class);
        String firstname = user.setFirstName(userDto.getFirstName());
        String lastname = user.setLastName(userDto.getLastName());
        user.setFullName(firstname +" " + lastname);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        Role role = this.roleRepo.findById(AppConstants.ROLE_BUYER).get();
        user.getRoles().add(role);
        user.setRole("ROLE_BUYER");
        user.setCreatedDate(LocalDateTime.now());

        user.setEnabled(false);
        otpService.generateOtp(user);

        if (userDto.getPicture() != null){
            user.setPicture(userDto.getPicture());
        }


        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalQuantity(0);
        cart.setTotalPrice(0.0);
        user.setCart(cart);


        User newUser = this.userRepository.save(user);
        return this.modelMapper.map(newUser, UserDto.class);
    }

    @Override
    public SellerDto registerNewSeller(SellerDto sellerDto){
        User user = this.modelMapper.map(sellerDto, User.class);
        String firstname = user.setFirstName(sellerDto.getFirstName());
        String lastname = user.setLastName(sellerDto.getLastName());
        user.setFullName(firstname + " " + lastname);

        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        Role role = this.roleRepo.findById(AppConstants.ROLE_SELLER).get();
        user.getRoles().add(role);
        user.setRole("ROLE_SELLER");

        user.setCreatedDate(LocalDateTime.now());

        if (sellerDto.getPicture() != null){
            user.setPicture(sellerDto.getPicture());
        }



        User newBuyer = this.userRepository.save(user);
        return this.modelMapper.map(newBuyer, SellerDto.class);

    }

    public UserDto userToDto(User user){
        UserDto userDto = this.modelMapper.map(user, UserDto.class);
        return userDto;
    }

    @Override
    public UserDto updateUser(UserUpdateDto userUpdateDto, Long userId){
        User user = userRepository.findById(userId).orElseThrow();

        if (userUpdateDto.getFirstName() != null && userUpdateDto.getLastName() !=null){
            user.setFirstName(userUpdateDto.getFirstName());
            user.setLastName(userUpdateDto.getLastName());
        }

        if (userUpdateDto.getMobileNumber() != null) {
            user.setMobileNumber(userUpdateDto.getMobileNumber());
        }

        if (userUpdateDto.getEmail() != null){
            user.setEmail(userUpdateDto.getEmail());
        }

        User updatedUser = userRepository.save(user);
        return userToDto(updatedUser);
    }

    @Override
    public UserDto getUserByUserName(String userToken) {
        String token = userToken.replaceAll("Bearer", " ").trim();
        String username = jwtTokenHelper.getUsernameFromToken(token);
        User user = this.userRepository.findByEmail(username).orElseThrow();
        UserDto userDto = userToDto(user);

        if (user.getPicture()!=null){
            String baseUrl = baseurl+"/api/v1/auth/picture/";
            userDto.setPictureUrl(baseUrl + user.getPicture());
        } else {
            user.setPicture("");
        }
        return userDto;
    }

    @Override
    public Map<String, Object> getAllUsers(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> users = this.userRepository.findAll(pageable);
        List<UserDto> userDtos = users.stream().map(user -> this.userToDto(user)).collect(Collectors.toList());

         Map<String, Object> response = new HashMap<>();
         response.put("data", userDtos);
         response.put("currentPage", users.getNumber());
         response.put("totalPages", users.getTotalPages());
         response.put("totalItems", users.getTotalElements());

        String Url = baseurl + "/api/v1/user/admin/users";
        response.put("firstPageUrl", Url + "?pageNumber=0&pageSize=" + pageSize);
        response.put("lastPageUrl", Url + "?pageNumber=" + (users.getTotalPages() - 1) + "&pageSize=" + pageSize);

        if (users.hasNext()){
            response.put("nextPageUrl", Url + "?pageNumber=" + (pageNumber + 1) + "&pageSize=" + pageSize);
        }
        if (users.hasPrevious()){
            response.put("prevPageUrl", Url + "?pageNumber=" + (pageNumber - 1) + "&pageSize=" + pageSize);
        }
        return response;
    }

    @Override
    public boolean existByMobileNumber(String mobileNumber) {
        return userRepository.existsByMobileNumber(mobileNumber);
    }

    @Override
    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDto changePassword(UserDto userDto, String userToken) {
        String token = userToken.replaceAll("Bearer", " ").trim();
        String username = jwtTokenHelper.getUsernameFromToken(token);
        User user = userRepository.findByEmail(username)
                .orElseThrow();
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            String encryptedPassword = this.passwordEncoder.encode(userDto.getPassword());
            user.setPassword(encryptedPassword);
        }
        User updatedUser = this.userRepository.save(user);
        return userToDto(updatedUser);
    }

    @Override
    public boolean verifyChangePassword(String userToken, String oldPassword) {
        String token = userToken.replaceAll("Bearer", " ").trim();
        String username = jwtTokenHelper.getUsernameFromToken(token);
        User user = userRepository.findByEmail(username)
                .orElseThrow();

        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public UserDto forgetPassword(UserDto userDto, String userToken) {
        String token = userToken.replaceAll("Bearer", " ").trim();
        String username = jwtTokenHelper.getUsernameFromToken(token);
        User user = this.userRepository.findByEmail(username).orElseThrow();

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()){
            String encryptedPassword = this.passwordEncoder.encode(userDto.getPassword());
            user.setPassword(encryptedPassword);
        }
        User updatedUser = this.userRepository.save(user);
        return userToDto(updatedUser);
    }

    @Override
    public boolean verifyForgetPassword(String userToken, String oldPassword) {
        String token = userToken.replaceAll("Bearer", " ").trim();
        String username = jwtTokenHelper.getUsernameFromToken(token);
        User user = userRepository.findByEmail(username).orElseThrow();
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }



}
