package com.ecommerce.reglog.repository;

import com.ecommerce.reglog.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartId(Long cartId);

    Optional<CartItem> findByCartIdAndProductIdAndSelectedSizeAndSelectedColor(
            Long cartId, Long productId, String selectedSize, String selectedColor);

    void deleteByCartId(Long cartId);
}
