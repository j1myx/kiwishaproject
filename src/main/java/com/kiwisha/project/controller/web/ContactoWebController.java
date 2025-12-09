package com.kiwisha.project.controller.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class ContactoWebController {
    @GetMapping("/contacto")
    public String contacto() {
        return "public/contacto";
    }

}
