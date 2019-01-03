package de.wasenweg.comix;

import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<String> scan() throws IOException {
    	List<String> comicFiles = ComicScanner.run();
    	List<Comic> comics = comicFiles.stream()
    			.map(path -> {
    				return new Comic(
    						path,
    						"FakeTitle",
    						"FakeSeries",
    						(short) 0,
    						(short) 2000,
    						(short) 10,
    						"FakePublisher"
    						);
    			})
    			.collect(Collectors.toList());
    	comicRepository.saveAll(comics);
    	return comicFiles;
    }
}