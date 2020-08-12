package com.zagwi.service;


import com.zagwi.domain.User;

public interface UserService {

    User findByUsername(String username);
}
