package io.cs702.bookbucket.search;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import io.cs702.bookbucket.book.BookUtils;
import reactor.core.publisher.Mono;

@Controller
public class SearchController {
    
    private final WebClient webClient;

    public SearchController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                    .defaultCodecs()
                    .maxInMemorySize(16 * 1024 * 1024 )) //16 Mb
                .build()) 
            .baseUrl("http://openlibrary.org/search.json")
            .build();
    }

    @GetMapping(value = "/search")
    public String getSearchResults(@RequestParam String query, Model model) {
        
        Mono<SearchResult> resultsMono = this.webClient.get()
        .uri("?q={query}", query)
        .retrieve().bodyToMono(SearchResult.class);

        SearchResult results = resultsMono.block();
        List<SearchResultBook> books = results.getDocs()
            .stream()
            .limit(10)
            .map(result -> {
                result.setCover_i(BookUtils.coverIdToURL(result.getCover_i(), "M"));
                result.setKey(result.getKey().replace("/works/", ""));
                return result;
            })
            .collect(Collectors.toList());
        model.addAttribute("results", books);
        
        return "search";
    }
}
