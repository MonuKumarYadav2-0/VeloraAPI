package in.scalive.Velora.service;

import java.util.List;

import in.scalive.Velora.dto.response.RatingResponseDTO;

public interface RatingService {

    // Add or update rating
    void rateProduct(Long userId, Long productId, int stars, String review);

    // Get average rating of product
    double getAverageRating(Long productId);

    // Get all ratings of a product
    List<RatingResponseDTO> getRatingsByProduct(Long productId);
}
