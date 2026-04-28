package in.scalive.Velora.contoller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.scalive.Velora.dto.response.ApiResponseDTO;
import in.scalive.Velora.dto.response.ProductResponseDTO;
import in.scalive.Velora.service.impl.FeedService;

@RestController
@Transactional
@RequestMapping("/api/feed")
@CrossOrigin(origins = "*")
public class FeedController {
	@Autowired
    private FeedService feedService;

    @GetMapping("{userId}")
    public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getPersonalizedFeed(@PathVariable Long userId) {
        List<ProductResponseDTO> feed = feedService.getPersonalizedFeed(userId);
        return ResponseEntity.ok(ApiResponseDTO.success(feed));
    }
}
