package com.abetappteam.abetapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping(value = {"/", "/{path:[^.]*}"})
    public String forward() {
        // forward all non-API requests to index.html
        return "forward:/index.html";
    }
}
