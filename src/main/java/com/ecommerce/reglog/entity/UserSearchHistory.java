package com.ecommerce.reglog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_search_history", indexes = {
    @Index(name = "idx_search_user", columnList = "user_id"),
    @Index(name = "idx_search_query", columnList = "search_query")
})
public class UserSearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "search_query", nullable = false, length = 200)
    private String searchQuery;

    @Column(name = "results_count")
    private Integer resultsCount = 0;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    public UserSearchHistory() {}

    public UserSearchHistory(User user, String searchQuery, Integer resultsCount) {
        this.user = user;
        this.searchQuery = searchQuery;
        this.resultsCount = resultsCount != null ? resultsCount : 0;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Integer getResultsCount() {
        return resultsCount;
    }

    public void setResultsCount(Integer resultsCount) {
        this.resultsCount = resultsCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
