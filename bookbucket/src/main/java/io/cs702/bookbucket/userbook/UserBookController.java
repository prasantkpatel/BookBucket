package io.cs702.bookbucket.userbook;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import io.cs702.bookbucket.book.Book;
import io.cs702.bookbucket.book.BookRepository;
import io.cs702.bookbucket.user.UserBooks;
import io.cs702.bookbucket.user.UserBooksRepository;

@Controller
public class UserBookController {
    
    @Autowired 
    private UserBookRepository userBookRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserBooksRepository userBooksRepository;

    @PostMapping("/addUserBook")
    public ModelAndView addBookForUser(@RequestBody MultiValueMap<String, String> formData, 
    @AuthenticationPrincipal OAuth2User principal) {
        ModelAndView redirectView = new ModelAndView("redirect:/");

        if(principal == null || principal.getAttribute("login") == null) {
            return redirectView;
        }
        UserBookPrimaryKey key = new UserBookPrimaryKey();

        String userId = principal.getAttribute("login");
        String bookId = formData.getFirst("bookId");
        LocalDate startedReading = LocalDate.parse(formData.getFirst("startedReading"));
        LocalDate finishedReading = LocalDate.parse(formData.getFirst("finishedReading"));
        String readingStatus = formData.getFirst("readingStatus");
        int rating = Integer.parseInt(formData.getFirst("rating"));

        key.setUserId(userId);
        key.setBookId(bookId);

        Optional<Book> optionalBook = bookRepository.findById(bookId);
        Book book;

        if(optionalBook.isPresent()) {
            book = optionalBook.get();
        } else {
            return redirectView;   
        }
        redirectView.setViewName("redirect:/book/" + bookId);

        UserBook userBook = new UserBook();

        userBook.setKey(key);
        userBook.setStartedReading(startedReading);
        userBook.setFinishedReading(finishedReading);
        userBook.setReadingStatus(readingStatus);
        userBook.setRating(rating);
        
        userBookRepository.save(userBook);

        UserBooks userBooks = new UserBooks();
        
        userBooks.setId(userId);
        userBooks.setBookId(bookId);
        userBooks.setReadingStatus(readingStatus);
        userBooks.setRating(rating);
        userBooks.setAuthorIds(book.getAuthorIds());
        userBooks.setAuthorNames(book.getAuthorNames());
        userBooks.setCoverIds(book.getCoverIds());
        userBooks.setBookName(book.getName());

        userBooksRepository.save(userBooks);

        return redirectView;
    }
}
