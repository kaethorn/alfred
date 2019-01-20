package de.wasenweg.komix;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

    @RequestMapping("/!api")
    public String redirect() {
        return "forward:/index.html";
    }
}
