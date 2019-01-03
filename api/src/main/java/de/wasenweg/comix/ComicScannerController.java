package de.wasenweg.comix;

import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("${spring.data.rest.base-path}")
@RestController
public class ComicScannerController {

	@Autowired
	private ComicRepository comicRepository;
	
    @RequestMapping("/scan")
    @ResponseBody
    public void scan() throws IOException {
    	List<Comic> comics = ComicScanner.run();
    	comicRepository.saveAll(comics);
    }
}