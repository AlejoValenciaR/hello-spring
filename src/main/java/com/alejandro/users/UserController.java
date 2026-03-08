package com.alejandro.users;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<String> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public String getUserById(@PathVariable Long id) {
        return userService.getUserByid(id);
    }
    
}
