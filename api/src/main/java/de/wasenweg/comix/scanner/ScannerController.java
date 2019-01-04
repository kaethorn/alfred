package de.wasenweg.comix.scanner;

import org.springframework.web.bind.annotation.RestController;

import de.wasenweg.comix.Comic;
import de.wasenweg.comix.ComicRepository;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("${spring.data.rest.base-path}")
@RestController
public class ScannerController {

	@Autowired
	private ComicRepository comicRepository;
	
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;
	
    @RequestMapping("/scan")
    @ResponseBody
    public void scan() throws IOException {
    	Scanner scanner = new Scanner(messagingTemplate);
    	// FIXME Fork here. This can run asynchronously.
    	List<Comic> comics = scanner.run();
    	comicRepository.saveAll(comics);
    }
}