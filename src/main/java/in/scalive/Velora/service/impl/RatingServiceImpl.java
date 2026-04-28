package in.scalive.Velora.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.scalive.Velora.dto.response.RatingResponseDTO;
import in.scalive.Velora.entity.Product;
import in.scalive.Velora.entity.Rating;
import in.scalive.Velora.entity.User;
import in.scalive.Velora.repository.ProductRepository;
import in.scalive.Velora.repository.RatingRepository;
import in.scalive.Velora.repository.UserRepository;
import in.scalive.Velora.service.RatingService;

@Service
public class RatingServiceImpl implements RatingService{

    @Autowired
    private RatingRepository ratingRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public void rateProduct(Long userId, Long productId, int stars, String review) {

        // check user & product
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // check existing rating
        Optional<Rating> existing = ratingRepo.findByUserIdAndProductId(userId, productId);

        if (existing.isPresent()) {
            Rating rating = existing.get();
            rating.setStars(stars);
            rating.setReview(review);
            ratingRepo.save(rating);
        } else {
            Rating rating = new Rating();
            rating.setUser(user);
            rating.setProduct(product);
            rating.setStars(stars);
            rating.setReview(review);
            ratingRepo.save(rating);
        }

        // update average
        updateAverageRating(productId);
    }

    @Override
    public double getAverageRating(Long productId) {
        List<Rating> ratings = ratingRepo.findByProductId(productId);

        return ratings.stream()
                .mapToInt(Rating::getStars)
                .average()
                .orElse(0.0);
    }

    private void updateAverageRating(Long productId) {
        double avg = getAverageRating(productId);

        Product product = productRepo.findById(productId).get();
        product.setAverageRating(avg);
        productRepo.save(product);
    }

	@Override
	public List<RatingResponseDTO> getRatingsByProduct(Long productId) {
		List<Rating> ratings=ratingRepo.findByProductId(productId);
		List<RatingResponseDTO> ratingDTO=new ArrayList<>();
		for(Rating rating:ratings)
		{
			RatingResponseDTO dto=RatingResponseDTO.builder()
					.userName(rating.getUser().getFullName())
					.stars(rating.getStars())
					.review(rating.getReview())
					.build();
			ratingDTO.add(dto);
		}
		return ratingDTO;
	}
}