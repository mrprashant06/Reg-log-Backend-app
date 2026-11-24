package com.ecommerce.reglog.repository.spec;

import com.ecommerce.reglog.dto.request.OrderFilterRequest;
import com.ecommerce.reglog.entity.OrderItem;
import com.ecommerce.reglog.entity.PurchaseOrder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<PurchaseOrder> build(Long userId, OrderFilterRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // User ownership
            predicates.add(cb.equal(root.get("user").get("id"), userId));

            // Status filter
            if (req.getOrderStatus() != null) {
                predicates.add(cb.equal(root.get("orderStatus"), req.getOrderStatus()));
            }

            // Payment method filter
            if (req.getPaymentMethod() != null) {
                predicates.add(cb.equal(root.get("paymentMethod"), req.getPaymentMethod()));
            }

            // Date Range filter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (req.getStartDate() != null && !req.getStartDate().isBlank()) {
                try {
                    LocalDate start = LocalDate.parse(req.getStartDate().trim(), formatter);
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"),
                            start.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                } catch (Exception ignored) {}
            }
            if (req.getEndDate() != null && !req.getEndDate().isBlank()) {
                try {
                    LocalDate end = LocalDate.parse(req.getEndDate().trim(), formatter);
                    predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"),
                            end.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                } catch (Exception ignored) {}
            }

            // Search query across Order Number, Product Name, Brand
            if (req.getQuery() != null && !req.getQuery().trim().isEmpty()) {
                query.distinct(true);
                String pattern = "%" + req.getQuery().trim().toLowerCase() + "%";
                Predicate orderNumberMatch = cb.like(cb.lower(root.get("orderNumber")), pattern);

                Join<PurchaseOrder, OrderItem> itemsJoin = root.join("items", JoinType.LEFT);
                Predicate productNameMatch = cb.like(cb.lower(itemsJoin.get("productName")), pattern);
                Predicate brandMatch = cb.like(cb.lower(itemsJoin.get("productBrand")), pattern);

                predicates.add(cb.or(orderNumberMatch, productNameMatch, brandMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
