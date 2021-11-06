package io.cs702.bookbucket.home;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.cs702.bookbucket.book.BookUtils;
import io.cs702.bookbucket.user.UserBooks;
import io.cs702.bookbucket.user.UserBooksRepository;

@Controller
public class HomeController {

    @Autowired
    UserBooksRepository userBooksRepository;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null || principal.getAttribute("login") == null) {
            return "index";
        }
        String userId = principal.getAttribute("login");
        Slice<UserBooks> userBooksSlice = userBooksRepository.findAllById(userId, CassandraPageRequest.of(0, 100));

        List<UserBooks> userBooks = userBooksSlice.getContent();
        userBooks = userBooks.stream().distinct().map(userBook -> {
            String coverImageURL = BookUtils.coverIdsToURL(userBook.getCoverIds(), "M");
            userBook.setCoverImageURL(coverImageURL);
            return userBook;
        }).collect(Collectors.toList());

        model.addAttribute("userBooks", userBooks);
        return "home";
    }
}
