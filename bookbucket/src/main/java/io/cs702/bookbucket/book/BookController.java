package io.cs702.bookbucket.book;

import java.util.Optional;
import java.util.Random;

import com.typesafe.config.ConfigException.Null;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BookController {
    private static final String COVER_IMG_URL = "http://covers.openlibrary.org/b/id/";
    private static final String NO_IMG_URL = "/images/no-img.png";
    private static final Random RNG = new Random();
    @Autowired BookRepository bookRepository;

    @GetMapping("/book/{bookId}")
    public String book(@PathVariable String bookId, Model bookModel) {
        System.out.println(bookId);
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if(optionalBook.isPresent()) {
           
            Book book = optionalBook.get();
            String coverImageURL =  NO_IMG_URL;
            if(book.getCoverIds() != null && book.getCoverIds().size() != 0) {
                String randomCoverId = book.getCoverIds()
                .get(RNG.nextInt(book.getCoverIds().size()));
                coverImageURL = COVER_IMG_URL + randomCoverId + "-L.jpg";
            }
            bookModel.addAttribute("book", book);
            bookModel.addAttribute("coverImageURL", coverImageURL);
            return "book";
        
        } else {
            return "resource-not-found";
        }
    }
}
