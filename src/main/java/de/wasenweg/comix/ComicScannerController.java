package de.wasenweg.comix;

import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("${spring.data.rest.base-path}")
@RestController
public class ComicScannerController {

    @RequestMapping("/scan")
    public List<String> scan() throws IOException {
    	return ComicScanner.run();
    }
}