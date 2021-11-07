package io.cs702.bookbucket;

import java.io.InputStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import io.cs702.bookbucket.configurations.DataStaxAstraConfig;

/**
 * Main application class with main method that runs the Spring Boot app
 */
@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraConfig.class)
public class BookbucketApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookbucketApplication.class, args);
	}

	/**
	 * This is necessary to have the Spring Boot app use the Astra secure bundle to
	 * connect to the database
	 */
	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraConfig astraConfig) {

		InputStream bundle = this.getClass().getClassLoader().getResourceAsStream(astraConfig.getSecureConnectBundle());
		return builder -> builder.withCloudSecureConnectBundle(bundle);

	}

}