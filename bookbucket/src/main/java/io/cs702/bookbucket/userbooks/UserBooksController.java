package io.cs702.bookbucket.userbooks;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserBooksController {
    
    @Autowired 
    private UserBooksRepository userBooksRepository;

    @PostMapping("/addUserBook")
    public ModelAndView addBookForUser(@RequestBody MultiValueMap<String, String> formData, 
    @AuthenticationPrincipal OAuth2User principal) {
        
        if(principal == null || principal.getAttribute("login") == null) {
            return null;
        }
        UserBooksPrimaryKey key = new UserBooksPrimaryKey();

        String userId = principal.getAttribute("login");
        String bookId = formData.getFirst("bookId");

        key.setUserId(userId);
        key.setBookId(bookId);

        UserBooks userBooks = new UserBooks();

        userBooks.setKey(key);
        userBooks.setStartedReading(LocalDate.parse(formData.getFirst("startedReading")));
        userBooks.setFinishedReading(LocalDate.parse(formData.getFirst("finishedReading")));
        userBooks.setReadingStatus(formData.getFirst("readingStatus"));
        userBooks.setRating(Integer.parseInt(formData.getFirst("rating")));
        
        userBooksRepository.save(userBooks);

        return new ModelAndView("redirect:/book/" + bookId);
    }
}
