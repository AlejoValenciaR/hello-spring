package com.alejandro.cv;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/portfolio")
public class CvController {

    @GetMapping("/alejandro")
    public String showAlejandroPortfolio() {
        return "cv/index6";
    }
}
