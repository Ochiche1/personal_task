package com.usersauth.usersauth.services;

import com.usersauth.usersauth.model.Users;
import com.usersauth.usersauth.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
   @Autowired
    UserRepository userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user =userRepo.findByUserName(username).orElseThrow(()
        -> new UsernameNotFoundException("User with username: " + username));
        return UserDetailsImpl.build(user);
    }


}
