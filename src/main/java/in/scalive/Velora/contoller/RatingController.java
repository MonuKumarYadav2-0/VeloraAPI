package in.scalive.Velora.contoller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.scalive.Velora.dto.request.RatingRequest;
import in.scalive.Velora.dto.response.ApiResponseDTO;
import in.scalive.Velora.dto.response.RatingResponseDTO;
import in.scalive.Velora.service.RatingService;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "*")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping("/rate")
    public ResponseEntity<ApiResponseDTO<String>> rateProduct( @RequestBody RatingRequest request) {

    	 System.out.println("UserId: " + request.getUserId());
         System.out.println("ProductId: " + request.getProductId());
         
        ratingService.rateProduct(
                request.getUserId(),
                request.getProductId(),
                request.getStars(),
                request.getReview()
        );

       
        return ResponseEntity.ok(ApiResponseDTO.success("Thanks for your feedback!"));
    }

    @GetMapping("/average/{productId}")
    public ResponseEntity<ApiResponseDTO<Double>>Average(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponseDTO.success(ratingService.getAverageRating(productId)));
    }
    
    @GetMapping("/{productId}")
  public ResponseEntity<ApiResponseDTO<List<RatingResponseDTO>>>  getRatingsByProduct(@PathVariable Long productId)
  {
	  List<RatingResponseDTO> dto=ratingService.getRatingsByProduct(productId);
	  return ResponseEntity.ok(ApiResponseDTO.success(dto));
  }
}