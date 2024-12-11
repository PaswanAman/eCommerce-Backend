package com.zosh.ecommerce.controller;


import com.zosh.ecommerce.Dto.*;
import com.zosh.ecommerce.config.JwtTokenHelper;
import com.zosh.ecommerce.config.MessageConstants;
import com.zosh.ecommerce.entities.*;
import com.zosh.ecommerce.repository.AdminRepo;
import com.zosh.ecommerce.repository.PredictionRepo;
import com.zosh.ecommerce.repository.StoreRepo;
import com.zosh.ecommerce.repository.UserRepo;
import com.zosh.ecommerce.service.AdminService;
import com.zosh.ecommerce.service.FileService;
import com.zosh.ecommerce.service.RefreshTokenService;
import com.zosh.ecommerce.service.UserService;
import com.zosh.ecommerce.serviceImpl.AdminUserDetailService;
import com.zosh.ecommerce.serviceImpl.CustomUserDetailService;
import com.zosh.ecommerce.serviceImpl.OtpService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
    private RefreshTokenService refreshTokenService;

    @Autowired
    private FileService fileService;
    @Value("${picture.base-url}")
    private String baseurl;
    @Autowired
    private PredictionRepo predictionRepo;
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

//    @PostMapping("/buyer/register")
//    public ResponseEntity<?> registerNewBuyer(@Valid @ModelAttribute UserDto userDto, @RequestParam("pictureFile") MultipartFile pictureFile, BindingResult bindingResult){
//        logger.info(" Register API called");
//        if (bindingResult.hasErrors()) {
//            List<String> errors = bindingResult.getFieldErrors().stream()
//                    .map(FieldError::getDefaultMessage)
//                    .collect(Collectors.toList());
//            return ResponseEntity.badRequest().body(errors);
//        }
//        Map<String, String> errorMap = new HashMap<>();
//
//        if (userDto.getFirstName() == null || userDto.getFirstName().equals("")){
//
//            errorMap.put("firstName", MessageConstants.MESSAGE_INVALIDFIRSTNAME);
//        }
//
//        if (userDto.getLastName() == null || userDto.getLastName().equals("")) {
//
//            errorMap.put("lastName", MessageConstants.MESSAGE_INVALIDLASTNAME);
//        }
//        if (userDto.getMobileNumber() == null || userDto.getMobileNumber().equals("") || userDto.getMobileNumber().length() != 10) {
//            errorMap.put("mobileNumber", MessageConstants.MESSAGE_INVALIDMOBILENUMBER);
//        }
//        if (userDto.getPassword() == null || userDto.getPassword().equals("") || userDto.getPassword().length() < 8 || userDto.getPassword().length()>20) {
//            errorMap.put("password", MessageConstants.MESSAGE_INVALIDPASSWORD);
//        }
//
//        if (!errorMap.isEmpty()) {
//            logger.info("ErrorMap is empty");
//            System.out.println(errorMap);
//            return ResponseEntity.badRequest().body(errorMap);
//        }
//
//        if (userService.existByMobileNumber(userDto.getMobileNumber())) {
//            String errorMessage = "Mobile number already exists";
//            Map<String, String> response = new HashMap<>();
//            logger.info("Mobile number already exists");
//            response.put("status","error");
//            response.put("message", errorMessage);
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        if (userService.existByEmail(userDto.getEmail())){
//            String errorMessage = "Email  already exists";
//            Map<String, String> response = new HashMap<>();
//            logger.info("Email is already exists");
//            response.put("status","error");
//            response.put("message", errorMessage);
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        if (!pictureFile.isEmpty()){
//            try {
//                String pictureFileName = fileService.savePicture(pictureFile);
//                userDto.setPicture(pictureFileName);
//                logger.info("Picture is set ");
//            }  catch (IOException e) {
//                logger.info("picture set error");
//                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            }
//        }
//
//        try {
//            UserDto registeredUser = userService.registerNewBuyer(userDto);
//            System.out.println("mobileNumber: "+userDto.getMobileNumber());
//            logger.info("User register successful");
//            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
//        } catch (IllegalArgumentException e) {
//            logger.info("User register error");
//            return new ResponseEntity<>(HttpStatus.CONFLICT);
//        }
//
//    }

//@PostMapping("/buyer/register")
//public ResponseEntity<?> registerNewBuyer(@Valid @ModelAttribute UserDto userDto,
//                                          @RequestParam("pictureFile") MultipartFile pictureFile,
//                                          BindingResult bindingResult) {
//    logger.info("Register API called");
//
//    if (bindingResult.hasErrors()) {
//        List<String> errors = bindingResult.getFieldErrors().stream()
//                .map(FieldError::getDefaultMessage)
//                .collect(Collectors.toList());
//        return ResponseEntity.badRequest().body(errors);
//    }
//
//    Map<String, String> errorMap = new HashMap<>();
//    if (userDto.getFirstName() == null || userDto.getFirstName().isEmpty()) {
//        errorMap.put("firstName", MessageConstants.MESSAGE_INVALIDFIRSTNAME);
//    }
//    if (userDto.getLastName() == null || userDto.getLastName().isEmpty()) {
//        errorMap.put("lastName", MessageConstants.MESSAGE_INVALIDLASTNAME);
//    }
//    if (userDto.getMobileNumber() == null || userDto.getMobileNumber().isEmpty() || userDto.getMobileNumber().length() != 10) {
//        errorMap.put("mobileNumber", MessageConstants.MESSAGE_INVALIDMOBILENUMBER);
//    }
//    if (userDto.getPassword() == null || userDto.getPassword().isEmpty() || userDto.getPassword().length() < 8 || userDto.getPassword().length() > 20) {
//        errorMap.put("password", MessageConstants.MESSAGE_INVALIDPASSWORD);
//    }
//    if (!errorMap.isEmpty()) {
//        return ResponseEntity.badRequest().body(errorMap);
//    }
//
//    if (userService.existByMobileNumber(userDto.getMobileNumber())) {
//        return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Mobile number already exists"));
//    }
//
//    if (userService.existByEmail(userDto.getEmail())) {
//        return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Email already exists"));
//    }
//
//    if (!pictureFile.isEmpty()) {
//        try {
//            String pictureFileName = fileService.savePicture(pictureFile);
//            userDto.setPicture(pictureFileName);
//
//            String imagePath = fileService.uploaddir() + "/" + pictureFileName;
//
//            sendImageToPythonApi(imagePath, userDto.getEmail());
//
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    try {
//        UserDto registeredUser = userService.registerNewBuyer(userDto);
//        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
//    } catch (IllegalArgumentException e) {
//        return new ResponseEntity<>(HttpStatus.CONFLICT);
//    }
//}
@PostMapping("/buyer/register")
public ResponseEntity<?> registerNewBuyer(@Valid @ModelAttribute UserDto userDto,
                                          @RequestParam("pictureFile") MultipartFile pictureFile,
                                          BindingResult bindingResult) {
    logger.info("Register API called");

    if (bindingResult.hasErrors()) {
        List<String> errors = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }

    Map<String, String> errorMap = new HashMap<>();
    if (userDto.getFirstName() == null || userDto.getFirstName().isEmpty()) {
        errorMap.put("firstName", MessageConstants.MESSAGE_INVALIDFIRSTNAME);
    }
    if (userDto.getLastName() == null || userDto.getLastName().isEmpty()) {
        errorMap.put("lastName", MessageConstants.MESSAGE_INVALIDLASTNAME);
    }
    if (userDto.getMobileNumber() == null || userDto.getMobileNumber().isEmpty() || userDto.getMobileNumber().length() != 10) {
        errorMap.put("mobileNumber", MessageConstants.MESSAGE_INVALIDMOBILENUMBER);
    }
    if (userDto.getPassword() == null || userDto.getPassword().isEmpty() || userDto.getPassword().length() < 8 || userDto.getPassword().length() > 20) {
        errorMap.put("password", MessageConstants.MESSAGE_INVALIDPASSWORD);
    }
    if (!errorMap.isEmpty()) {
        return ResponseEntity.badRequest().body(errorMap);
    }

    if (userService.existByMobileNumber(userDto.getMobileNumber())) {
        return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Mobile number already exists"));
    }

    if (userService.existByEmail(userDto.getEmail())) {
        return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Email already exists"));
    }

    String imagePath = null;

    if (!pictureFile.isEmpty()) {
        try {
            String pictureFileName = fileService.savePicture(pictureFile);
            userDto.setPicture(pictureFileName);

            imagePath = fileService.uploaddir() + "/" + pictureFileName;

            sendImageToPythonApi(imagePath, userDto.getEmail());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    if (imagePath != null) {
        Predictions currentPrediction = predictionRepo.findByFilePath(userDto.getPicture());
        if (currentPrediction != null) {
            int personId = currentPrediction.getPersonId();
            List<Predictions> predictionsList = predictionRepo.findByPersonId(personId);
            long countWithEmail = predictionsList.stream()
                    .filter(p -> p.getEmail() != null && !p.getEmail().isEmpty())
                    .count();
            if (countWithEmail > 1) {
                predictionRepo.deleteById(currentPrediction.getId());

                return ResponseEntity.badRequest()
                        .body(Map.of("status", "error", "message", "Person is already registered with a different email."));
            }
        }
    }


    try {
        UserDto registeredUser = userService.registerNewBuyer(userDto);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}

    public void sendImageToPythonApi(String imagePath, String email) {
        try {
            File imageFile = new File(imagePath);

            if (!imageFile.exists()) {
                throw new FileNotFoundException("Image file not found: " + imagePath);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("files", new FileSystemResource(imageFile));
            body.add("email", email);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            String pythonApiUrl = "http://3.110.123.38:8000/upload/";
            ResponseEntity<String> response = restTemplate.postForEntity(pythonApiUrl, requestEntity, String.class);

            System.out.println("Python API response: " + response.getBody());

        } catch (Exception e) {
            System.err.println("Error sending image to Python API");
            e.printStackTrace();
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
        Optional<User> employee = userRepo.findByEmail(request.getEmail());
        UserResponse response = new UserResponse();
        if (employee.isPresent()){
            User employee1 = employee.get();
            System.out.println(employee1.getEmail());
            try {
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));


                if (authentication.isAuthenticated()){
                    RefreshToken refreshToken = refreshTokenService.createRefreshToken(employee1.getEmail());
                    Date tokenExpiryDate = jwtTokenHelper.getExpirationDateFromToken(jwtTokenHelper.generateToken(employee1.getEmail(),"user"));
                    long expiryInSeconds = (tokenExpiryDate.getTime() - System.currentTimeMillis()) / 1000;
                    response.setToken(jwtTokenHelper.generateToken(employee1.getEmail(),"user"));
                    response.setRefreshToken(refreshToken.getRefreshToken());
                    response.setMessage("Login Successful");
                    response.setUserId(employee1.getId());
                    response.setFirstName(employee1.getFirstName());

                    response.setLastName(employee1.getLastName());
                    response.setEmail(employee1.getEmail());
                    response.setRole(employee1.getRole());
                    response.setTokenExpiryTime(String.valueOf(expiryInSeconds));
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
                    .body(Map.of("status", "error", "message", "Employee not found"));
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


//    @PostMapping("/buyer/login")
//    public ResponseEntity<?> buyerLogin(@RequestBody JwtAuthRequest request){
//        logger.info("Buyer  Login API called ");
//        Optional<User> user = userRepo.findByEmail(request.getEmail());
//        UserResponse response = new UserResponse();
//        if (user.isPresent()){
//            User user1 = user.get();
//            System.out.println(user1.getEmail());
//            System.out.println(user1.getRole());
//            if (!"ROLE_BUYER".equalsIgnoreCase(user1.getRole())) {
//                logger.info("User does not have buyer role");
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(Map.of("status", "error", "message", "Only buyers are allowed to log in."));
//            }
//            try {
//                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
//
//
//                if (authentication.isAuthenticated()){
//
//                    response.setToken(jwtTokenHelper.generateToken(user.get().getEmail(), "buyer"));
//                    response.setMessage("Login Successful");
//                    response.setFirstName(user1.getFirstName());
//                    response.setLastName(user1.getLastName());
//                    response.setEmail(user1.getEmail());
//                    response.setUserId(user1.getId());
//                    response.setRole(user1.getRole());
//                    response.setPictureUrl(baseurl+"/api/v1/auth/picture/"+user1.getPicture());
//                    logger.info("Login Successful");
//                    return new ResponseEntity<>(response, HttpStatus.OK);
//                } else {
//                    logger.info("Invalid email or password");
//                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                            .body(Map.of("status", "error", "message","Invalid Email or Passowrd. Please try again."));
//                }
//            }  catch (AuthenticationException e) {
//                logger.info("Invalid email or Password!");
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(Map.of("status", "error", "message", "Invalid email or password. Please try again."));
//            }
//        } else {
//            logger.info("Buyer not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("status", "error", "message", "User not found"));
//        }
//
//    }
@PostMapping("/buyer/login")
public ResponseEntity<?> buyerLogin(
        @ModelAttribute JwtAuthRequest request
         ) {

    logger.info("Buyer Login API called");
//    if (imageFile != null && !imageFile.isEmpty()) {
//        try {
//            String matchedEmail = sendImageToPythonApiLogin(imageFile);
//
//            if (matchedEmail != null) {
//                Optional<User> user = userRepo.findByEmail(matchedEmail);
//                if (user.isPresent()) {
//                    return generateLoginResponse(user.get());
//                } else {
//
//                    logger.warn("No user found for matched_email: " + matchedEmail);
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                            .body(Map.of("status", "error", "message", "User not found for matched image"));
//                }
//            } else {
//                logger.warn("Face not recognized in the provided image");
//                String fileName = imageFile.getOriginalFilename();
//                Predictions predictions = predictionRepo.findByFilePath(fileName);
//                predictionRepo.deleteById(predictions.getId());
//
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(Map.of("status", "error", "message", "Face not recognized. Please try again."));
//            }
//        } catch (Exception e) {
//            logger.error("Error during image-based login: ", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("status", "error", "message", "Error processing image login"));
//        }
//    }
//
//    if (request.getEmail() == null || request.getPassword() == null) {
//        return ResponseEntity.badRequest()
//                .body(Map.of("status", "error", "message", "Email and password are required for login"));
//    }

    Optional<User> employee = userRepo.findByEmail(request.getEmail());
    UserResponse response = new UserResponse();
    if (employee.isPresent()){
        User employee1 = employee.get();
        System.out.println(employee1.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));


            if (authentication.isAuthenticated()){
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(employee1.getEmail());
                Date tokenExpiryDate = jwtTokenHelper.getExpirationDateFromToken(jwtTokenHelper.generateToken(employee1.getEmail(),"user"));
                long expiryInSeconds = (tokenExpiryDate.getTime() - System.currentTimeMillis()) / 1000;
                response.setToken(jwtTokenHelper.generateToken(employee1.getEmail(),"user"));
                response.setRefreshToken(refreshToken.getRefreshToken());
                response.setMessage("Login Successful");
                response.setUserId(employee1.getId());
                response.setFirstName(employee1.getFirstName());

                    response.setLastName(employee1.getLastName());
                response.setEmail(employee1.getEmail());
                response.setRole(employee1.getRole());
                response.setTokenExpiryTime(String.valueOf(expiryInSeconds));
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
                .body(Map.of("status", "error", "message", "Employee not found"));
    }
}

    private String sendImageToPythonApiLogin(MultipartFile imageFile) throws IOException {
        File tempFile = convertToFile(imageFile);

        try {
            logger.info("Temporary file created: " + tempFile.getAbsolutePath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("files", new FileSystemResource(tempFile));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            String pythonApiUrl = "http://3.110.123.38:8000/upload/";
            ResponseEntity<Map> response = restTemplate.postForEntity(pythonApiUrl, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> pythonResponse = response.getBody();
                List<Map<String, String>> results = (List<Map<String, String>>) pythonResponse.get("results");

                if (!results.isEmpty()) {
                    return results.get(0).get("matched_email");
                }
            }
        } catch (Exception e) {
            logger.error("Error while sending image to Python API: ", e);
            throw new RuntimeException("Failed to process image for login");
        } finally {
            if (tempFile.exists() && !tempFile.delete()) {
                logger.warn("Failed to delete temporary file: " + tempFile.getAbsolutePath());
            }
        }

        return null;
    }

    private File convertToFile(MultipartFile file) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");

        File convFile = new File(tempDir, file.getOriginalFilename());


        if (convFile.exists()) {
            convFile.delete();
        }

        // Write the content to the file
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }

        return convFile;
    }



    private ResponseEntity<UserResponse> generateLoginResponse(User user, RefreshToken refreshToken) {
        UserResponse response = new UserResponse();
        response.setToken(jwtTokenHelper.generateToken(user.getEmail(), "buyer"));
        response.setRefreshToken(String.valueOf(refreshToken));
        response.setMessage("Login Successful");
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setUserId(user.getId());
        response.setRole(user.getRole());
        response.setPictureUrl(baseurl + "/api/v1/auth/picture/" + user.getPicture());
        logger.info("Login Successful");
        return new ResponseEntity<>(response, HttpStatus.OK);
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
            Otp otp = otpService.getToken(request.getOtpCode());
            if (otp == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status","error","message","OTP not found or expired"));
            }
            User user = otp.getUser();
            user.setEnabled(true);
            userRepo.save(user);

            otpService.deleteToken(otp);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("status","success","message","Otp verify successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public UserResponse refreshToken(@RequestBody RefreshTokenRequest request){
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        User employee =  refreshToken.getUser();
        String token = this.jwtTokenHelper.generateToken(employee.getEmail(),"employee");
        Date tokenExpiryDate = jwtTokenHelper.getExpirationDateFromToken(token);

        return UserResponse.builder().refreshToken(refreshToken.getRefreshToken())
                .token(token)
                .message("New Jwt Token")
                .email(employee.getEmail())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .role(employee.getRole())
                .tokenExpiryTime(tokenExpiryDate.toString())
                .build();

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
