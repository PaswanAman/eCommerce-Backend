package com.zosh.ecommerce.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String mobileNumber;
    private String email;
    private String password;
    @Column(name = "Picture")
    private String picture;
    private String role;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdDate;

    private boolean isVerified = false;  // Mark as true after OTP verification
    private boolean isEnabled = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    @OneToOne(mappedBy = "seller", cascade = CascadeType.ALL)
    private Store store;

    @OneToMany(mappedBy = "seller")
    private List<Product> products;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private Set<Otp> otps = new HashSet<>();


    public String setFirstName(String firstName) {

        if (firstName != null && !firstName.isEmpty()) {
            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
        }
        this.firstName = firstName;
        return firstName;
    }

    public String setLastName(String lastName) {

        if (lastName != null && !lastName.isEmpty()) {
            lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
        }
        this.lastName = lastName;
        return lastName;
    }







}
