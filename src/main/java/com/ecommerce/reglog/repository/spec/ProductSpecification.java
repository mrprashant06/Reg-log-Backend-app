package com.ecommerce.reglog.repository.spec;

import com.ecommerce.reglog.dto.request.ProductFilterRequest;
import com.ecommerce.reglog.entity.Product;
import com.ecommerce.reglog.entity.ProductVariant;
import com.ecommerce.reglog.enums.Gender;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> build(ProductFilterRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Only active products
            predicates.add(cb.isTrue(root.get("isActive")));

            // Search query across name, brand, description
            if (req.getQuery() != null && !req.getQuery().trim().isEmpty()) {
                String searchPattern = "%" + req.getQuery().trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("name")), searchPattern);
                Predicate brandMatch = cb.like(cb.lower(root.get("brand")), searchPattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), searchPattern);
                predicates.add(cb.or(nameMatch, brandMatch, descMatch));
            }

            // Category filter
            if (req.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), req.getCategoryId()));
            } else if (req.getCategorySlug() != null && !req.getCategorySlug().isBlank()) {
                predicates.add(cb.equal(root.get("category").get("slug"), req.getCategorySlug().trim()));
            } else if (req.getCategoryIds() != null && !req.getCategoryIds().isEmpty()) {
                predicates.add(root.get("category").get("id").in(req.getCategoryIds()));
            }

            // Price range filter
            if (req.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("sellingPrice"), req.getMinPrice()));
            }
            if (req.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("sellingPrice"), req.getMaxPrice()));
            }

            // Brand multi-selection
            if (req.getBrands() != null && !req.getBrands().isEmpty()) {
                List<Predicate> brandPreds = new ArrayList<>();
                for (String brand : req.getBrands()) {
                    brandPreds.add(cb.equal(cb.lower(root.get("brand")), brand.trim().toLowerCase()));
                }
                predicates.add(cb.or(brandPreds.toArray(new Predicate[0])));
            }

            // Gender filter
            if (req.getGender() != null && !req.getGender().isBlank() && !"ALL".equalsIgnoreCase(req.getGender())) {
                try {
                    Gender g = Gender.valueOf(req.getGender().trim().toUpperCase());
                    predicates.add(cb.or(
                            cb.equal(root.get("gender"), g),
                            cb.equal(root.get("gender"), Gender.UNISEX)
                    ));
                } catch (Exception ignored) {}
            }

            // Variants filter: Color and/or Size
            boolean hasColors = req.getColors() != null && !req.getColors().isEmpty();
            boolean hasSizes = req.getSizes() != null && !req.getSizes().isEmpty();

            if (hasColors || hasSizes) {
                query.distinct(true);
                Join<Product, ProductVariant> variantJoin = root.join("variants", JoinType.LEFT);

                if (hasColors) {
                    List<Predicate> colorPreds = new ArrayList<>();
                    for (String c : req.getColors()) {
                        colorPreds.add(cb.equal(cb.lower(variantJoin.get("color")), c.trim().toLowerCase()));
                    }
                    predicates.add(cb.or(colorPreds.toArray(new Predicate[0])));
                }

                if (hasSizes) {
                    List<Predicate> sizePreds = new ArrayList<>();
                    for (String s : req.getSizes()) {
                        sizePreds.add(cb.equal(cb.lower(variantJoin.get("size")), s.trim().toLowerCase()));
                    }
                    predicates.add(cb.or(sizePreds.toArray(new Predicate[0])));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
