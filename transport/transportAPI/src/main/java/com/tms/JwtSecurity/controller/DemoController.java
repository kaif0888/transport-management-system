package com.tms.JwtSecurity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String adminAccess() {
        return "Admin access granted.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER')")
    public String userAccess() {
        return "User access granted.";
    }
}