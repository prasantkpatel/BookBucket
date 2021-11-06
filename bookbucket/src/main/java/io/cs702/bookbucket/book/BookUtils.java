package io.cs702.bookbucket.book;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import io.cs702.bookbucket.author.Author;
import io.cs702.bookbucket.author.AuthorRepository;
import io.cs702.bookbucket.author.AuthorUtils;
import io.cs702.bookbucket.spring.SpringUtils;
import reactor.core.publisher.Mono;

public class BookUtils {
    private static final String COVER_IMG_URL = "http://covers.openlibrary.org/b/id/";
    private static final String NO_IMG_URL = "/images/no-img.png";
    private static final Random RNG = new Random();
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    private static AuthorRepository authorRepository = SpringUtils.getBean(AuthorRepository.class);
    private static BookRepository bookRepository = SpringUtils.getBean(BookRepository.class);
    private static WebClient webClient = WebClient.create("https://openlibrary.org/");

    public static String coverIdsToURL(List<String> coverIds, String size) {
        String coverImageURL = NO_IMG_URL;
        if (coverIds != null && coverIds.size() != 0) {
            String randomCoverId = coverIds.get(RNG.nextInt(coverIds.size()));
            coverImageURL = coverIdToURL(randomCoverId, size);
        }
        return coverImageURL;
    }

    public static String coverIdToURL(String coverId, String size) {
        String coverImageURL = NO_IMG_URL;
        if (StringUtils.hasText(coverId)) {
            coverImageURL = COVER_IMG_URL + coverId + "-" + size + ".jpg";
        }
        return coverImageURL;
    }

    public static Book parseBook(String jsonString) {
        try {
            if (jsonString == null) {
                return null;
            }
            JSONObject bookJson = new JSONObject(jsonString);
            Book book = new Book();
            book.setId(bookJson.optString("key").replace("/works/", ""));
            book.setName(bookJson.optString("title"));

            JSONObject descriptionJson = bookJson.optJSONObject("description");
            if (descriptionJson != null) {
                book.setDescription(descriptionJson.optString("value"));
            }

            JSONObject publishedDateJson = bookJson.optJSONObject("created");
            if (publishedDateJson != null) {
                book.setPublishedDate(LocalDate.parse(publishedDateJson.optString("value"), DATE_TIME_FORMAT));
            }

            JSONArray coverIdsJsonArray = bookJson.optJSONArray("covers");
            if (coverIdsJsonArray != null) {
                List<String> coverIds = new ArrayList<>();
                for (int i = 0; i < coverIdsJsonArray.length(); ++i) {
                    coverIds.add(coverIdsJsonArray.getString(i));
                }
                book.setCoverIds(coverIds);
            }

            JSONArray authorJsonArray = bookJson.optJSONArray("authors");
            if (authorJsonArray != null) {

                List<String> authorIds = new ArrayList<>();
                for (int i = 0; i < authorJsonArray.length(); ++i) {
                    JSONObject authorJsonObject = authorJsonArray.getJSONObject(i);
                    String authorId = authorJsonObject.getJSONObject("author").getString("key").replace("/authors/",
                            "");
                    authorIds.add(authorId);
                }
                book.setAuthorIds(authorIds);

                List<String> authorNames = new ArrayList<>();
                authorIds.stream().forEach(authorId -> {
                    Optional<Author> optionalAuthor = authorRepository.findById(authorId);
                    if (optionalAuthor.isPresent()) {
                        authorNames.add(optionalAuthor.get().getName());

                    } else {
                        Mono<String> monoAuthor = webClient.get().uri("authors/{authorId}.json", authorId).retrieve()
                                .bodyToMono(String.class);

                        String jsonAuthor = monoAuthor.block();
                        Author author = AuthorUtils.parseAuthor(jsonAuthor);
                        if (author != null) {
                            authorRepository.save(author);
                            authorNames.add(author.getName());
                        } else {
                            authorNames.add("Unknown Author");
                        }
                    }
                });
                book.setAuthorNames(authorNames);
            }
            return book;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Book getAndSave(String bookId) {

        Mono<String> monoBook = webClient.get().uri("works/{bookId}.json", bookId).retrieve().bodyToMono(String.class);

        Book book = new Book();
        String bookJson = monoBook.block();
        book = parseBook(bookJson);

        if (book != null)
            bookRepository.save(book);

        return book;
    }
}
