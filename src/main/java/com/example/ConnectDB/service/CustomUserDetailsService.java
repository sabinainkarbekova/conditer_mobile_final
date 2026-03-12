package com.example.ConnectDB.service;

import com.example.ConnectDB.model.Role;
import com.example.ConnectDB.model.User;
import com.example.ConnectDB.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Получите роль из пользователя (enum Role)
        Role userRole = user.getRole(); // Это уже enum Role, по умолчанию USER согласно вашей модели
        if (userRole == null) {
            userRole = Role.USER; // роль по умолчанию как enum
        }

        // Преобразуйте enum в строку с префиксом для Spring Security
        String authority = "ROLE_" + userRole.name(); // например: "ROLE_ADMIN" или "ROLE_USER"

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authority)
                .build();
    }
}
