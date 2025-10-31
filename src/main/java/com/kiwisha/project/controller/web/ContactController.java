package com.kiwisha.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContactController {
    @GetMapping("/contacto")
    public String contacto() {
        return "user/contacto"; // Thymeleaf busca src/main/resources/templates/user/contacto.html
    }
}
