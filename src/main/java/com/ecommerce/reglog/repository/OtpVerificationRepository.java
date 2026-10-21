package com.ecommerce.reglog.repository;

import com.ecommerce.reglog.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByIdentifierAndPurposeAndIsConsumedFalseOrderByCreatedAtDesc(
            String identifier, String purpose
    );

    Optional<OtpVerification> findTopByIdentifierOrderByCreatedAtDesc(String identifier);
}
