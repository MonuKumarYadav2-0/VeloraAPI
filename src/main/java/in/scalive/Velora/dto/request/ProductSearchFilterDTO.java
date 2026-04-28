package in.scalive.Velora.dto.request;

public class ProductSearchFilterDTO {
	    private String name;
	    private String category;
	    private String brand;
	    private Double minPrice;
	    private Double maxPrice;
	    private String message;

	    // Getters & Setters
	    public String getName() { return name; }
	    public void setName(String name) { this.name = name; }

	    public String getCategory() { return category; }
	    public void setCategory(String category) { this.category = category; }

	    public String getBrand() { return brand; }
	    public void setBrand(String brand) { this.brand = brand; }

	    public Double getMinPrice() { return minPrice; }
	    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }

	    public Double getMaxPrice() { return maxPrice; }
	    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }
	    
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
	    
	    
}
