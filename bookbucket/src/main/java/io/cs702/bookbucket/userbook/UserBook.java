package io.cs702.bookbucket.userbook;

import java.time.LocalDate;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;

/**
 * Model that represents a user's interaction with a specific book.
*/
@Table("book_by_user_and_book_id")
public class UserBook {
    
    @PrimaryKey
    private UserBookPrimaryKey key;

    @Column("started_reading")
    @CassandraType(type = Name.DATE)
    private LocalDate startedReading;

    @Column("finished_reading")
    @CassandraType(type = Name.DATE)
    private LocalDate finishedReading;
    
    @Column("reading_status")
    @CassandraType(type = Name.TEXT)
    private String readingStatus;

    @Column("rating")
    @CassandraType(type = Name.INT)
    private int rating;

    public UserBookPrimaryKey getKey() {
        return key;
    }

    public void setKey(UserBookPrimaryKey key) {
        this.key = key;
    }

    public LocalDate getStartedReading() {
        return startedReading;
    }

    public void setStartedReading(LocalDate startedReading) {
        this.startedReading = startedReading;
    }

    public LocalDate getFinishedReading() {
        return finishedReading;
    }

    public void setFinishedReading(LocalDate finishedReading) {
        this.finishedReading = finishedReading;
    }

    public String getReadingStatus() {
        return readingStatus;
    }

    public void setReadingStatus(String readingStatus) {
        this.readingStatus = readingStatus;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

}
