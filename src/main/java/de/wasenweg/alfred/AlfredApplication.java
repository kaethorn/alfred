package de.wasenweg.alfred;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@SpringBootApplication
public class AlfredApplication implements ErrorController {

  private static final String ERROR_PATH = "/error";

  public static void main(final String[] args) {
    SpringApplication.run(AlfredApplication.class, args);
  }

  @RequestMapping(ERROR_PATH)
  public String error() {
    return "forward:/index.html";
  }

  @Override
  public String getErrorPath() {
    return ERROR_PATH;
  }
}
