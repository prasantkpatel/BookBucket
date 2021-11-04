package io.cs702.bookbucket.book;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface BookRepository extends CassandraRepository<Book, String> {
    
}