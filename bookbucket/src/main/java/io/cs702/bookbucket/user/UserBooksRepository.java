package io.cs702.bookbucket.user;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface UserBooksRepository extends CassandraRepository<UserBooks, String> {

    public Slice<UserBooks> findAllById(String id, Pageable pageable);
}
