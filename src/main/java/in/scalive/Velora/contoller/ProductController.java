package in.scalive.Velora.contoller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.scalive.Velora.dto.request.ProductRequestDTO;
import in.scalive.Velora.dto.request.UpdateProductRequestDTO;
import in.scalive.Velora.dto.response.ApiResponseDTO;
import in.scalive.Velora.dto.response.PageResponseDTO;
import in.scalive.Velora.dto.response.ProductResponseAIDTO;
import in.scalive.Velora.dto.response.ProductResponseDTO;
import in.scalive.Velora.service.ProductService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
   private ProductService serv;
   
   @Autowired
   public ProductController(ProductService serv) {
	this.serv = serv;
   }
   
   @PreAuthorize("hasRole('ADMIN')")
   @PostMapping
   public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO){
	   ProductResponseDTO responseDTO= serv.createProduct(productRequestDTO);
	   ApiResponseDTO<ProductResponseDTO> obj=ApiResponseDTO.success("product created Successfully",responseDTO);
	   return new ResponseEntity<>(obj,HttpStatus.CREATED); 
   }
   
   @GetMapping("/{id}")
   public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> getProductById(@PathVariable("id") Long id) {
	   ProductResponseDTO responseDTO=serv.getProductById(id);
	   return ResponseEntity.ok(ApiResponseDTO.success(responseDTO));
   }
   
   
   @GetMapping("/sku/{sku}")
   public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> getProductBySku(@PathVariable("sku") String sku) {
	   ProductResponseDTO responseDTO=serv.getProductBySku(sku);
	   return ResponseEntity.ok(ApiResponseDTO.success(responseDTO));
   }
   
   @GetMapping("/all")
   public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getAllProducts(){
	   List<ProductResponseDTO> productsList=serv.getAllProducts();
	   return ResponseEntity.ok(ApiResponseDTO.success("fetched "+productsList.size()+" products",productsList));
   }
   
   @GetMapping("/latest")
   public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getLatestProducts(){
	   List<ProductResponseDTO> productsList=serv.getLatestProducts();
	   return ResponseEntity.ok(ApiResponseDTO.success("fetched "+productsList.size()+" products",productsList));
   }
   
   @GetMapping()
   public ResponseEntity<ApiResponseDTO<PageResponseDTO<ProductResponseDTO>>> getAllProductsPaginated(@RequestParam(defaultValue = "0") int page,
		   @RequestParam(defaultValue = "10") int size,
		   @RequestParam(defaultValue = "id") String sortBy,
		   @RequestParam(defaultValue = "asc") String sortDir) {
	   PageResponseDTO<ProductResponseDTO> pageResponseDTO=serv.getAllProductsPaginated(page, size, sortBy, sortDir);
	   return ResponseEntity.ok(ApiResponseDTO.success(pageResponseDTO));
   }
   
   @GetMapping("/available")
   public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getAvailableProducts() {
	   List<ProductResponseDTO> productsList=serv.getAvailableProducts();
	   return ResponseEntity.ok(ApiResponseDTO.success(productsList));
   }
   
   @GetMapping("/category/{category}")
   public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getProductsByCategory(@PathVariable String category) {
	   List<ProductResponseDTO> productsList=serv.getProductsByCategory(category);
	   return ResponseEntity.ok(ApiResponseDTO.success(productsList));
   }
   
   @GetMapping("/price-range")
   public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getProductsByPriceRange(@RequestParam double min,@RequestParam double max){
	   List<ProductResponseDTO> productsList=serv.getProductsByPriceRange(min, max);
	   return ResponseEntity.ok(ApiResponseDTO.success(productsList));
   }
   
   @GetMapping("/search")
   public ResponseEntity<ApiResponseDTO<PageResponseDTO<ProductResponseDTO>>> searchProducts(@RequestParam String keyword,
		   @RequestParam(defaultValue = "0") int page,
		   @RequestParam(defaultValue = "10") int size){
	   PageResponseDTO<ProductResponseDTO> pageResponseDTO=serv.searchProducts(keyword, page, size);
	   return ResponseEntity.ok(ApiResponseDTO.success(pageResponseDTO));
   }
   
   
   @PutMapping("/{id}")
   public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> udpateProduct(@PathVariable Long id,@Valid @RequestBody UpdateProductRequestDTO updateProductRequestDTO){
	   ProductResponseDTO responseDTO=serv.updateProduct(id,updateProductRequestDTO);
	   return ResponseEntity.ok(ApiResponseDTO.success("product updated Successfully",responseDTO));
   }
   
   @PreAuthorize("hasRole('ADMIN')")
   @PatchMapping("/{id}/stock")
   public ResponseEntity<ApiResponseDTO<Void>> updateStock(@PathVariable Long id,@RequestParam Integer quantity){
	   serv.updateStock(id, quantity);
	   return ResponseEntity.ok(ApiResponseDTO.success("stock updated successfully"));
   }
   
   @PreAuthorize("hasRole('ADMIN')")
   @GetMapping("/low-stock")
   public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getLowStockProducts(@RequestParam(defaultValue = "10") Integer threshold){
	   List<ProductResponseDTO> productsList=serv.getLowStockProducts(threshold);
	   return ResponseEntity.ok(ApiResponseDTO.success(productsList));
   }
   
   @GetMapping("/check-sku/{sku}")
   public ResponseEntity<ApiResponseDTO<Boolean>> checkSkuExists(@PathVariable String sku){
	   Boolean res=serv.existsBySku(sku);
	   return ResponseEntity.ok(ApiResponseDTO.success(res?"sku already exists":"sku is available to use",res));
   }
   
   @PostMapping("/search/ai")
   public ResponseEntity<ApiResponseDTO<ProductResponseAIDTO>> aiSearch(@RequestBody Map<String, String> request) {
       String prompt = request.get("prompt");

       if (prompt == null || prompt.isBlank()) {
           return ResponseEntity.badRequest().build();
       }

       ProductResponseAIDTO productResponseAIDTO = serv.searchProductsByPrompt(prompt);
       return ResponseEntity.ok(ApiResponseDTO.success(productResponseAIDTO));
   }
}
