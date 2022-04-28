package de.wasenweg.alfred;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;

@Controller
public class IndexController implements ErrorController {

  public String error() {
    return "forward:/index.html";
  }
}
