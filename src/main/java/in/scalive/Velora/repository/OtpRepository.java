package in.scalive.Velora.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.scalive.Velora.entity.OtpVerification;

public interface OtpRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findByEmail(String email);

    void deleteByEmail(String email); // 🔥 ADD THIS
}