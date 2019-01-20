package de.wasenweg.komix;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

    @RequestMapping({ "/comics/**",  "/read/**", "/preferences/**", "/read-full-screen/**" })
    public String redirect() {
        return "forward:/index.html";
    }
}
