package io.cs702.bookbucket.userbook;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface UserBookRepository extends CassandraRepository<UserBook, UserBookPrimaryKey> {
    
}
