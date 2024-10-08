package com.zosh.ecommerce.controller;


import com.zosh.ecommerce.Dto.*;
import com.zosh.ecommerce.config.JwtTokenHelper;
import com.zosh.ecommerce.config.MessageConstants;
import com.zosh.ecommerce.entities.Admin;
import com.zosh.ecommerce.entities.Store;
import com.zosh.ecommerce.entities.User;
import com.zosh.ecommerce.exception.OtpNotFoundException;
import com.zosh.ecommerce.exception.UserNotFoundException;
import com.zosh.ecommerce.repository.AdminRepo;
import com.zosh.ecommerce.repository.StoreRepo;
import com.zosh.ecommerce.repository.UserRepo;
import com.zosh.ecommerce.service.AdminService;
import com.zosh.ecommerce.service.FileService;
import com.zosh.ecommerce.service.UserService;
import com.zosh.ecommerce.serviceImpl.AdminUserDetailService;
import com.zosh.ecommerce.serviceImpl.CustomUserDetailService;
import com.zosh.ecommerce.serviceImpl.OtpService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")

public class AuthController {

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private CustomUserDetailService userDetailsService;

    @Autowired
    private AdminUserDetailService adminUserDetailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AdminService adminService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AdminRepo adminRepo;
    @Autowired
    private StoreRepo storeRepo;

    @Autowired
    private FileService fileService;
    @Value("${picture.base-url}")
    private String baseurl;

    private Logger logger = LoggerFactory.getLogger(AuthController.class);


    // For Admin
    @PostMapping("/admin/register")
    public ResponseEntity<?> registerAdmin(@RequestBody AdminDto adminDto){
        Map<String, String> errorMap = new HashMap<>();

        if (adminDto.getFirstName() == null || adminDto.getFirstName().equals("")){

            errorMap.put("firstName", MessageConstants.MESSAGE_INVALIDFIRSTNAME);
        }

        if (adminDto.getLastName() == null || adminDto.getLastName().equals("")) {

            errorMap.put("lastName", MessageConstants.MESSAGE_INVALIDLASTNAME);
        }
        if (adminDto.getMobileNumber() == null || adminDto.getMobileNumber().equals("") || adminDto.getMobileNumber().length() != 10) {
            errorMap.put("mobileNumber", MessageConstants.MESSAGE_INVALIDMOBILENUMBER);
        }
        if (adminDto.getPassword() == null || adminDto.getPassword().equals("") || adminDto.getPassword().length() < 8 || adminDto.getPassword().length()>20) {
            errorMap.put("password", MessageConstants.MESSAGE_INVALIDPASSWORD);
        }

        if (!errorMap.isEmpty()) {
            logger.info("ErrorMap is empty");
            System.out.println(errorMap);
            return ResponseEntity.badRequest().body(errorMap);
        }

        try {
            AdminDto registeredAdmin = adminService.registerAdmin(adminDto);
            System.out.println("mobileNumber: "+adminDto.getMobileNumber());
            logger.info("Admin register successful");
            return new ResponseEntity<>(registeredAdmin, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.info("Admin register error");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }


    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody JwtAuthRequest request){
        logger.info("Seller  Login API called ");
        Optional<Admin> admin = adminRepo.findByEmail(request.getEmail());
        JwtResponse response = new JwtResponse();
        if (admin.isPresent()){
            Admin admin1 = admin.get();
            System.out.println(admin1.getEmail());
            try {
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));


                if (authentication.isAuthenticated()){

                    response.setToken(jwtTokenHelper.generateToken(admin.get().getEmail(), "admin"));
                    response.setMessage("Login Successful");
                    response.setFirstName(admin1.getFirstName());
                    response.setLastName(admin1.getLastName());
                    response.setEmail(admin1.getEmail());
                    response.setUserId(admin1.getId());
                    logger.info("Login Successful");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    logger.info("Invalid email or password");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("status", "error", "message","Invalid Email or Passowrd. Please try again."));
                }
            }  catch (AuthenticationException e) {
                logger.info("Invalid email or Password!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "error", "message", "Invalid email or password. Please try again."));
            }
        } else {
            logger.info("Admin not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Admin not found"));
        }

    }



    // For New User or Buyer

    @PostMapping("/buyer/register")
    public ResponseEntity<?> registerNewBuyer(@Valid @ModelAttribute UserDto userDto, @RequestParam("pictureFile") MultipartFile pictureFile, BindingResult bindingResult){
        logger.info(" Register API called");
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        Map<String, String> errorMap = new HashMap<>();

        if (userDto.getFirstName() == null || userDto.getFirstName().equals("")){

            errorMap.put("firstName", MessageConstants.MESSAGE_INVALIDFIRSTNAME);
        }

        if (userDto.getLastName() == null || userDto.getLastName().equals("")) {

            errorMap.put("lastName", MessageConstants.MESSAGE_INVALIDLASTNAME);
        }
        if (userDto.getMobileNumber() == null || userDto.getMobileNumber().equals("") || userDto.getMobileNumber().length() != 10) {
            errorMap.put("mobileNumber", MessageConstants.MESSAGE_INVALIDMOBILENUMBER);
        }
        if (userDto.getPassword() == null || userDto.getPassword().equals("") || userDto.getPassword().length() < 8 || userDto.getPassword().length()>20) {
            errorMap.put("password", MessageConstants.MESSAGE_INVALIDPASSWORD);
        }

        if (!errorMap.isEmpty()) {
            logger.info("ErrorMap is empty");
            System.out.println(errorMap);
            return ResponseEntity.badRequest().body(errorMap);
        }



        if (userService.existByMobileNumber(userDto.getMobileNumber())) {
            String errorMessage = "Mobile number already exists";
            Map<String, String> response = new HashMap<>();
            logger.info("Mobile number already exists");
            response.put("status","error");
            response.put("message", errorMessage);
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.existByEmail(userDto.getEmail())){
            String errorMessage = "Email  already exists";
            Map<String, String> response = new HashMap<>();
            logger.info("Email is already exists");
            response.put("status","error");
            response.put("message", errorMessage);
            return ResponseEntity.badRequest().body(response);
        }

        if (!pictureFile.isEmpty()){
            try {
                String pictureFileName = fileService.savePicture(pictureFile);
                userDto.setPicture(pictureFileName);
                logger.info("Picture is set ");
            }  catch (IOException e) {
                logger.info("picture set error");
                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        try {
            UserDto registeredUser = userService.registerNewBuyer(userDto);
            System.out.println("mobileNumber: "+userDto.getMobileNumber());
            logger.info("User register successful");
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.info("User register error");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }





    // For Seller

    @PostMapping("/seller/register")
    public ResponseEntity<?> registerUser(@Valid @ModelAttribute SellerDto sellerDto, @RequestParam("pictureFile") MultipartFile pictureFile, BindingResult bindingResult){
        logger.info(" Register API called");
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        Map<String, String> errorMap = new HashMap<>();

        if (sellerDto.getFirstName() == null || sellerDto.getFirstName().equals("")){

            errorMap.put("firstName", MessageConstants.MESSAGE_INVALIDFIRSTNAME);
        }

        if (sellerDto.getLastName() == null || sellerDto.getLastName().equals("")) {

            errorMap.put("lastName", MessageConstants.MESSAGE_INVALIDLASTNAME);
        }
        if (sellerDto.getMobileNumber() == null || sellerDto.getMobileNumber().equals("") || sellerDto.getMobileNumber().length() != 10) {
            errorMap.put("mobileNumber", MessageConstants.MESSAGE_INVALIDMOBILENUMBER);
        }
        if (sellerDto.getPassword() == null || sellerDto.getPassword().equals("") || sellerDto.getPassword().length() < 8 || sellerDto.getPassword().length()>20) {
            errorMap.put("password", MessageConstants.MESSAGE_INVALIDPASSWORD);
        }

        if (!errorMap.isEmpty()) {
            logger.info("ErrorMap is empty");
            System.out.println(errorMap);
            return ResponseEntity.badRequest().body(errorMap);
        }



        if (userService.existByMobileNumber(sellerDto.getMobileNumber())) {
            String errorMessage = "Mobile number already exists";
            Map<String, String> response = new HashMap<>();
            logger.info("Mobile number already exists");
            response.put("error", errorMessage);
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.existByEmail(sellerDto.getEmail())){
            String errorMessage = "Email is already exists";
            Map<String, String> response = new HashMap<>();
            logger.info("Email is already exists");
            response.put("error", errorMessage);
            return ResponseEntity.badRequest().body(response);
        }

        if (!pictureFile.isEmpty()){
            try {
                String pictureFileName = fileService.savePicture(pictureFile);
                sellerDto.setPicture(pictureFileName);
                logger.info("Picture is set ");
            }  catch (IOException e) {
                logger.info("picture set error");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        try {
            SellerDto registeredSeller = userService.registerNewSeller(sellerDto);
            System.out.println("mobileNumber: "+sellerDto.getMobileNumber());
            logger.info("User register successful");
            return new ResponseEntity<>(registeredSeller, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.info("User register error");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }

    @PostMapping("/seller/login")
    public ResponseEntity<?> sellerLogin(@RequestBody JwtAuthRequest request){
        logger.info("Seller  Login API called ");
        Optional<User> user = userRepo.findByEmail(request.getEmail());
        UserResponse response = new UserResponse();
        if (user.isPresent()){
            User user1 = user.get();
            System.out.println(user1.getEmail());
            if (!"ROLE_SELLER".equalsIgnoreCase(user1.getRole())) {
                logger.info("User does not have seller role");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("status", "error", "message", "Only seller are allowed to log in."));
            }


            try {
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));


                if (authentication.isAuthenticated()){

                    response.setToken(jwtTokenHelper.generateToken(user.get().getEmail(), "seller"));
                    response.setMessage("Login Successful");
                    response.setFirstName(user1.getFirstName());
                    response.setLastName(user1.getLastName());
                    response.setEmail(user1.getEmail());
                    response.setUserId(user1.getId());
                    response.setRole(user1.getRole());
                    response.setPictureUrl(baseurl+"/api/v1/auth/picture/"+user1.getPicture());

                    Optional<Store> store = storeRepo.findBySeller(user1);
                    if (store.isPresent()){
                        response.setStoreId(store.get().getId());
                    } else {
                        response.setStoreId(null);
                    }
                    logger.info("Login Successful");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    logger.info("Invalid email or password");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("status", "error", "message","Invalid Email or Passowrd. Please try again."));
                }
            }  catch (AuthenticationException e) {
                logger.info("Invalid email or Password!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "error", "message", "Invalid email or password. Please try again."));
            }
        } else {
            logger.info("Employee not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User not found"));
        }

    }


    @GetMapping("/picture/{pictureFileName}")
    public ResponseEntity<Resource> getPicture(@PathVariable String pictureFileName){
        logger.info("Set Picture API called");
        try {
            String uploadDir = fileService.uploaddir();
            Path picturePath = Paths.get(uploadDir, pictureFileName);
            Resource resource = new UrlResource(picturePath.toUri());

            if (resource.exists() && resource.isReadable()){
                logger.info("Image Upload");
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                logger.info("Image doesn't upload");
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e){
            return ResponseEntity.badRequest().build();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/buyer/login")
    public ResponseEntity<?> buyerLogin(@RequestBody JwtAuthRequest request){
        logger.info("Buyer  Login API called ");
        Optional<User> user = userRepo.findByEmail(request.getEmail());
        UserResponse response = new UserResponse();
        if (user.isPresent()){
            User user1 = user.get();
            System.out.println(user1.getEmail());
            System.out.println(user1.getRole());
            if (!"ROLE_BUYER".equalsIgnoreCase(user1.getRole())) {
                logger.info("User does not have buyer role");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("status", "error", "message", "Only buyers are allowed to log in."));
            }
            try {
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));


                if (authentication.isAuthenticated()){

                    response.setToken(jwtTokenHelper.generateToken(user.get().getEmail(), "buyer"));
                    response.setMessage("Login Successful");
                    response.setFirstName(user1.getFirstName());
                    response.setLastName(user1.getLastName());
                    response.setEmail(user1.getEmail());
                    response.setUserId(user1.getId());
                    response.setRole(user1.getRole());
                    response.setPictureUrl(baseurl+"/api/v1/auth/picture/"+user1.getPicture());
                    logger.info("Login Successful");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    logger.info("Invalid email or password");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("status", "error", "message","Invalid Email or Passowrd. Please try again."));
                }
            }  catch (AuthenticationException e) {
                logger.info("Invalid email or Password!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "error", "message", "Invalid email or password. Please try again."));
            }
        } else {
            logger.info("Buyer not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User not found"));
        }

    }

    @PutMapping ("/changePassword")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String userToken, @RequestBody ChangePassword changePassword){
        logger.info("ChangePassword API called");

        String oldPassword = changePassword.getOldPassword();
        String newPassword = changePassword.getNewPassword();
        String confirmPassword = changePassword.getConfirmPassword();

        if (!userService.verifyChangePassword(userToken, oldPassword)) {
            logger.info("Verify change Password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status","error", "message","old password doesn't match"));
        }

        // Validate that the new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            logger.info("New Password and Confirm matched");
            return ResponseEntity.badRequest().body(Map.of("status", "error","message","newPassword and ConfirmPassword doesn't match"));
        }

        UserDto userDto = new UserDto();
        userDto.setPassword(newPassword);

        UserDto updatedUser = userService.changePassword(userDto, userToken);
        logger.info("Password changed successfully");
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("status","success","message","Password changed"));


    }

    @PutMapping("/forget-password")
    public ResponseEntity<?>forgetPassword(@RequestHeader("Authorization") String userToken, @RequestBody ChangePassword changePassword){
        logger.info("Forget Password API called");
        String newPassword = changePassword.getNewPassword();
        String confirmPassword = changePassword.getConfirmPassword();
        if (!newPassword.equals(confirmPassword)){
            logger.info("NewPassword and ConfirmPassword doesn't matched");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "error", "message", "NewPassword and ConfirmPassword doesn't match"));
        }
        UserDto userDto = new UserDto();
        userDto.setPassword(newPassword);

        UserDto updatedUser = userService.forgetPassword(userDto,userToken);
        logger.info("Password changed Successfully");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "success", "message","Password changed successfully"));

    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request) {
        try {
            otpService.verifyOtp(request.getOtpCode());
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("status","success","message","Otp verify successfully"));
        } catch (OtpNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status","error","message","OTP not found or expired: " + e.getMessage()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status","error","message","User not found: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    private void authenticate(String email, String password) {
        logger.info("Authenticate email and password api called");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
                password);
        try {
            this.authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            logger.info("Invalid Username or Password!!");
             throw new BadCredentialsException("Invalid Username or Password !!");
        }
    }
}
