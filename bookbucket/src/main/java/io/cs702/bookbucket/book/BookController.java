package io.cs702.bookbucket.book;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;

import io.cs702.bookbucket.author.AuthorRepository;
import reactor.core.publisher.Mono;

@Controller
public class BookController {

    @Autowired BookRepository bookRepository;
    @Autowired AuthorRepository authorRepository;

    private WebClient webClient;

    public BookController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("http://openlibrary.org/")
            .build();
    }

    @GetMapping("/book/{bookId}")
    public String book(@PathVariable String bookId, Model bookModel) {
        System.out.println(bookId);
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        Book book;
        if(optionalBook.isPresent()) {    
            book = optionalBook.get();
    
        } else {
            Mono<String> monoBook = this.webClient.get()
                .uri("works/{bookId}.json", bookId)
                .retrieve()
                .bodyToMono(String.class);
            
            String bookJson = monoBook.block();
            book = BookUtils.parseBook(bookJson);
            if(book != null) {
                bookRepository.save(book);
            } else {
                return "book-not-found";
            }
        }
        String coverImageURL =  BookUtils.coverIdsToURL(book.getCoverIds(), "L");
        bookModel.addAttribute("book", book);
        bookModel.addAttribute("coverImageURL", coverImageURL);
        return "book";
    }
    
}
