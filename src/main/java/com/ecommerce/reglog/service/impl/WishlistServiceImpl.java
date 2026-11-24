package com.ecommerce.reglog.service.impl;

import com.ecommerce.reglog.dto.request.AddToCartRequest;
import com.ecommerce.reglog.dto.response.WishlistResponse;
import com.ecommerce.reglog.entity.Product;
import com.ecommerce.reglog.entity.User;
import com.ecommerce.reglog.entity.Wishlist;
import com.ecommerce.reglog.entity.WishlistItem;
import com.ecommerce.reglog.exception.ResourceNotFoundException;
import com.ecommerce.reglog.repository.ProductRepository;
import com.ecommerce.reglog.repository.UserRepository;
import com.ecommerce.reglog.repository.WishlistItemRepository;
import com.ecommerce.reglog.repository.WishlistRepository;
import com.ecommerce.reglog.service.CartService;
import com.ecommerce.reglog.service.WishlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public WishlistServiceImpl(WishlistRepository wishlistRepository,
                               WishlistItemRepository wishlistItemRepository,
                               UserRepository userRepository,
                               ProductRepository productRepository,
                               CartService cartService) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    private Wishlist getOrCreateWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            Wishlist newWishlist = new Wishlist(user);
            return wishlistRepository.save(newWishlist);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public WishlistResponse getWishlist(Long userId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        return WishlistResponse.fromEntity(wishlist);
    }

    @Override
    @Transactional
    public WishlistResponse addToWishlist(Long userId, Long productId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        boolean exists = wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), productId);
        if (!exists) {
            WishlistItem item = new WishlistItem(wishlist, product);
            wishlist.getItems().add(item);
            wishlistItemRepository.save(item);
            wishlist = wishlistRepository.save(wishlist);
        }
        return WishlistResponse.fromEntity(wishlist);
    }

    @Override
    @Transactional
    public WishlistResponse removeFromWishlist(Long userId, Long productId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        wishlist.getItems().removeIf(item -> item.getProduct() != null && item.getProduct().getId().equals(productId));
        wishlistItemRepository.deleteByWishlistIdAndProductId(wishlist.getId(), productId);
        wishlist = wishlistRepository.save(wishlist);
        return WishlistResponse.fromEntity(wishlist);
    }

    @Override
    @Transactional
    public void clearWishlist(Long userId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        wishlist.getItems().clear();
        wishlistRepository.save(wishlist);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInWishlist(Long userId, Long productId) {
        return wishlistRepository.findByUserId(userId)
                .map(w -> wishlistItemRepository.existsByWishlistIdAndProductId(w.getId(), productId))
                .orElse(false);
    }

    @Override
    @Transactional
    public WishlistResponse moveToCart(Long userId, Long productId, String size, String color) {
        // 1. Add to cart
        AddToCartRequest req = new AddToCartRequest();
        req.setProductId(productId);
        req.setQuantity(1);
        req.setSelectedSize(size);
        req.setSelectedColor(color);
        cartService.addToCart(userId, req);

        // 2. Remove from wishlist
        return removeFromWishlist(userId, productId);
    }
}
