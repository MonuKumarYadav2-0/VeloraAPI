package in.scalive.Velora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.scalive.Velora.entity.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByProductId(Long productId);

    Optional<Rating> findByUserIdAndProductId(Long userId, Long productId);
}