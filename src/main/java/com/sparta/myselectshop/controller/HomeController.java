package com.sparta.myselectshop.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class HomeController {

    @GetMapping("/")
    public String home(){
        return "index";
    }
}
