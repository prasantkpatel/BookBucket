package io.cs702.bookbucket.book;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;

import io.cs702.bookbucket.author.AuthorRepository;
import io.cs702.bookbucket.userbooks.UserBooks;
import io.cs702.bookbucket.userbooks.UserBooksPrimaryKey;
import io.cs702.bookbucket.userbooks.UserBooksRepository;
import reactor.core.publisher.Mono;

@Controller
public class BookController {

    @Autowired BookRepository bookRepository;
    @Autowired AuthorRepository authorRepository;
    @Autowired UserBooksRepository userBooksRepository;

    private WebClient webClient;

    public BookController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("http://openlibrary.org/")
            .build();
    }

    @GetMapping("/book/{bookId}")
    public String book(@PathVariable String bookId, Model model, 
    @AuthenticationPrincipal OAuth2User principal) {
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
        model.addAttribute("book", book);
        model.addAttribute("coverImageURL", coverImageURL);

        if(principal != null && principal.getAttribute("login") != null) {
            
            String userId = principal.getAttribute("login");
            model.addAttribute("loginId", userId);
            
            UserBooksPrimaryKey key = new UserBooksPrimaryKey();
            key.setUserId(userId);
            key.setBookId(bookId);

            Optional<UserBooks> userBooks = userBooksRepository.findById(key);

            if(userBooks.isPresent()) {
                model.addAttribute("userBooks", userBooks.get());
            } else {
                model.addAttribute("userBooks", new UserBooks());
            }
        }
        return "book";
    }
    
}
