package com.kiwisha.project.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutWebController {

    @GetMapping("/nosotros")
    public String about() {
        return "public/nosotros";
    }
}
