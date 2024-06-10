package com.usersauth.usersauth.controller;

import com.usersauth.usersauth.Enum.Enum_Roles;
import com.usersauth.usersauth.model.Roles;
import com.usersauth.usersauth.model.Users;
import com.usersauth.usersauth.payload.Requests.LoginRequest;
import com.usersauth.usersauth.payload.Requests.SignupRequest;
import com.usersauth.usersauth.payload.Responses.MessageResponse;
import com.usersauth.usersauth.payload.Responses.UserInfoResponse;
import com.usersauth.usersauth.repository.RoleRepository;
import com.usersauth.usersauth.repository.UserRepository;
import com.usersauth.usersauth.security.JwtUtils;
import com.usersauth.usersauth.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import javax.validation.Valid;
import javax.ws.rs.core.HttpHeaders;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).toList();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .body(new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(), userDetails.getEmail(), roles));
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser (@Valid @RequestBody SignupRequest signupRequest){
        if (userRepository.existsByUsername(signupRequest.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("Username already Exist"));
        }
        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Email already in use"));
        }
        Users users = new Users(signupRequest.getUsername(),
                                signupRequest.getEmail(),
                                encoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRoles();
        Set<Roles> roles = new HashSet<>();
        if (strRoles == null) {
            Roles userRoles = roleRepository.findByName(Enum_Roles.USERS).orElseThrow(() -> new RuntimeException(
                "Role user not found"));
            roles.add(userRoles);

        }else {
            strRoles.forEach(role -> {
                switch (role){
                    case "Admin":
                        Roles adminRole = roleRepository.findByName(Enum_Roles.ADMIN).orElseThrow(()-> new RuntimeException(
                            "Role admin not found"));
                        roles.add(adminRole);
                        break;
                    case "Editor":
                        Roles editorRole = roleRepository.findByName(Enum_Roles.EDITOR).orElseThrow(()-> new RuntimeException(
                            "Role Editor does not exist"));
                        roles.add(editorRole);
                        break;
                    default:
                        Roles userRoles = roleRepository.findByName(Enum_Roles.USERS).orElseThrow(() -> new RuntimeException(
                            "Role user not found"));
                        roles.add(userRoles);
                }
            });
        }
        users.setRoles(roles);
        userRepository.save(users);
        return ResponseEntity.ok(new MessageResponse("User registration was successful"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(){
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(new MessageResponse("You've successfully logout of the system"));
    }
}
