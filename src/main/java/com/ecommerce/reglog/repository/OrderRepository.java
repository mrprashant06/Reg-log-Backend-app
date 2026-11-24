package com.ecommerce.reglog.repository;

import com.ecommerce.reglog.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<PurchaseOrder, Long>, JpaSpecificationExecutor<PurchaseOrder> {

    Page<PurchaseOrder> findByUserId(Long userId, Pageable pageable);

    Optional<PurchaseOrder> findByIdAndUserId(Long id, Long userId);

    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);

    Optional<PurchaseOrder> findByOrderNumberAndUserId(String orderNumber, Long userId);
}
