package com.abetappteam.abetapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    // Map root path
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    // Map all other paths except API, static resources, and files with extensions
    @GetMapping(value = {"/{path:(?!api|assets|h2-console)[^\\.]*}", "/{path:(?!api|assets|h2-console)[^\\.]*}/**"})
    public String forward() {
        // forward all non-API requests to index.html for Vue Router
        return "forward:/index.html";
    }
}