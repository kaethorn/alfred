package de.wasenweg.comix;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class ComicScannerController {

    @RequestMapping("/scan")
    public String scan() {
    	return ComicScanner.run();
    }
}