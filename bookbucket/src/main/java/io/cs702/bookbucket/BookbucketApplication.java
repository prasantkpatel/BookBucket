package io.cs702.bookbucket;

import java.nio.file.Path;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import io.cs702.bookbucket.configurations.DataStaxAstraConfig;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraConfig.class)
public class BookbucketApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookbucketApplication.class, args);
	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraConfig astraConfig) {
		Path bundle = astraConfig.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}
	
}