package in.scalive.Velora.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(nullable = false,length = 200)
    private String name;
	
	@Column(length = 1000)
    private String description;
	
	@Column(nullable = false)
    private Double price;
	
	@Column(nullable = false)
	@Builder.Default
    private Integer stockQuantity=0;
	
	@Column(length = 100)
    private String category;
	
	@Column(length = 100)
    private String brand;
	
	@Column(length = 500)
    private String imageUrl;
	
	@Column(unique = true,length = 50)
    private String sku;
	
	@Column(nullable = false)
	@Builder.Default
    private Boolean isAvailable=true; //for doing soft delete
	
	@OneToMany(mappedBy = "product")
	private List<Rating> ratings;
	
	@Column(name = "model_url")
	private String modelUrl;
	
	private double averageRating;
}
