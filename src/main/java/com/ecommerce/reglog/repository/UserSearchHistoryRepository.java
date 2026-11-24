package com.ecommerce.reglog.repository;

import com.ecommerce.reglog.entity.UserSearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSearchHistoryRepository extends JpaRepository<UserSearchHistory, Long> {
    List<UserSearchHistory> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
}
