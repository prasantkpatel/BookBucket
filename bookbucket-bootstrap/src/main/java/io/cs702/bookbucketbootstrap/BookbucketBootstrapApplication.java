package io.cs702.bookbucketbootstrap;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import io.cs702.bookbucketbootstrap.author.Author;
import io.cs702.bookbucketbootstrap.author.AuthorRepository;
import io.cs702.bookbucketbootstrap.book.Book;
import io.cs702.bookbucketbootstrap.book.BookRepository;
import io.cs702.bookbucketbootstrap.configurations.DataStaxAstraConfig;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraConfig.class)
public class BookbucketBootstrapApplication {

	@Autowired
	AuthorRepository authorRepository;
	@Autowired
	BookRepository bookRepository;

	@Value("${datadump.location.author}")
	private String authorDumpLocation;

	@Value("${datadump.location.works}")
	private String worksDumpLocation;

	public static void main(String[] args) {
		SpringApplication.run(BookbucketBootstrapApplication.class, args);
	}

	private void initAuthor() {
		try {
			Path path = Paths.get(getClass().getClassLoader().getResource(authorDumpLocation).toURI());
			try (Stream<String> lines = Files.lines(path)) {
				lines.forEach(line -> {

					String jsonString = line.substring(line.indexOf("{"));
					try {
						JSONObject authorJson = new JSONObject(jsonString);

						Author author = new Author();
						author.setName(authorJson.optString("name"));
						author.setPersonalName(authorJson.optString("personal_name"));
						author.setId(authorJson.optString("key").replace("/authors/", ""));

						System.out.println("Saving author " + author.getName() + "...");
						authorRepository.save(author);

					} catch (JSONException e) {
						e.printStackTrace();
					}

				});

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void initWorks() {
		try {
			Path path = Paths.get(getClass().getClassLoader().getResource(worksDumpLocation).toURI());
			DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
			try (Stream<String> lines = Files.lines(path)) {
				lines.forEach(line -> {
					try {
						JSONObject bookJson = new JSONObject(line.substring(line.indexOf("{")));
						Book book = new Book();

						book.setId(bookJson.optString("key").replace("/works/", ""));
						book.setName(bookJson.optString("title"));

						JSONObject descriptionJson = bookJson.optJSONObject("description");
						if (descriptionJson != null) {
							book.setDescription(descriptionJson.optString("value"));
						}

						JSONObject publishedDateJson = bookJson.optJSONObject("created");
						if (publishedDateJson != null) {
							book.setPublishedDate(
									LocalDate.parse(publishedDateJson.optString("value"), dateTimeFormat));
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
							List<String> authorNames = new ArrayList<>();

							for (int i = 0; i < authorJsonArray.length(); ++i) {
								JSONObject authorJsonObject = authorJsonArray.getJSONObject(i);
								String authorId = authorJsonObject.getJSONObject("author").getString("key")
										.replace("/authors/", "");

								Optional<Author> optionalAuthor = authorRepository.findById(authorId);
								if (optionalAuthor.isPresent()) {
									authorNames.add(optionalAuthor.get().getName());
								} else {
									authorNames.add("Unknown Author");
								}
							}

							book.setAuthorIds(authorIds);
							book.setAuthorNames(authorNames);
						}

						System.out.println("Saving book " + book.getName() + "...");
						bookRepository.save(book);

					} catch (JSONException e) {
						e.printStackTrace();
					}
				});

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@PostConstruct
	public void start() {
		initAuthor();
		initWorks();
	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraConfig astraConfig) {
		Path bundle = astraConfig.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

}
