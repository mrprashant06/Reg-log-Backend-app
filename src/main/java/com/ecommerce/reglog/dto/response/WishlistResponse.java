package com.ecommerce.reglog.dto.response;

import com.ecommerce.reglog.entity.Wishlist;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WishlistResponse {

    private Long id;
    private int totalItems;
    private List<WishlistItemResponse> items = new ArrayList<>();

    public WishlistResponse() {}

    public static WishlistResponse fromEntity(Wishlist w) {
        WishlistResponse res = new WishlistResponse();
        res.setId(w.getId());
        if (w.getItems() != null) {
            res.setItems(w.getItems().stream()
                    .map(WishlistItemResponse::fromEntity)
                    .collect(Collectors.toList()));
            res.setTotalItems(res.getItems().size());
        } else {
            res.setTotalItems(0);
        }
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<WishlistItemResponse> getItems() {
        return items;
    }

    public void setItems(List<WishlistItemResponse> items) {
        this.items = items;
        this.totalItems = items != null ? items.size() : 0;
    }
}
