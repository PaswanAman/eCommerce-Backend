package com.zosh.ecommerce.controller;

import com.zosh.ecommerce.Dto.UserDto;
import com.zosh.ecommerce.exception.ResourceNotFoundException;
import com.zosh.ecommerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;


    @GetMapping("/admin/users")
     public  Map<String, Object> getAllUsers(@RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "5") Integer pageSize){
        logger.info("Get All Users API called");
        logger.info("Get All Users");
         return userService.getAllUsers(pageNumber, pageSize);
     }

     @GetMapping("/SingleUser")
     public ResponseEntity<?> getSingleUser(@RequestHeader("Authorization") String userToken){
        logger.info("Get Single User API called");
        try{
              UserDto userDto = this.userService.getUserByUserName(userToken);
              logger.info("Get Single User Successfully");
              return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", "success", "Message", userDto));
        }catch (ResourceNotFoundException e){
            logger.info("Error to Get Single User");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "error", "message", e.getMessage()));

        }

     }
}
