package io.cs702.bookbucket.book;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import io.cs702.bookbucket.userbook.UserBook;
import io.cs702.bookbucket.userbook.UserBookPrimaryKey;
import io.cs702.bookbucket.userbook.UserBookRepository;

@Controller
public class BookController {

    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserBookRepository userBookRepository;

    @GetMapping("/book/{bookId}")
    public String book(@PathVariable String bookId, Model model, @AuthenticationPrincipal OAuth2User principal) {
        System.out.println(bookId);
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        Book book;
        if (optionalBook.isPresent()) {
            book = optionalBook.get();

        } else {
            book = BookUtils.getAndSave(bookId);

            if (book == null) {
                return "book-not-found";
            }
        }
        String coverImageURL = BookUtils.coverIdsToURL(book.getCoverIds(), "L");
        model.addAttribute("book", book);
        model.addAttribute("coverImageURL", coverImageURL);

        if (principal != null && principal.getAttribute("login") != null) {

            String userId = principal.getAttribute("login");
            model.addAttribute("loginId", userId);

            UserBookPrimaryKey key = new UserBookPrimaryKey();
            key.setUserId(userId);
            key.setBookId(bookId);

            Optional<UserBook> userBook = userBookRepository.findById(key);

            if (userBook.isPresent()) {
                model.addAttribute("userBook", userBook.get());
            } else {
                model.addAttribute("userBook", new UserBook());
            }
        }
        return "book";
    }

}
