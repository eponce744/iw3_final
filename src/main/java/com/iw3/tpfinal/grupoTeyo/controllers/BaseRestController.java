package com.iw3.tpfinal.grupoTeyo.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.iw3.tpfinal.grupoTeyo.auth.User;

public class BaseRestController {
    protected User getUserLogged() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return user;
    }
}
