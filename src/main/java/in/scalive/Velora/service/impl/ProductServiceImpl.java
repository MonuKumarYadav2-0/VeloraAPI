package in.scalive.Velora.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.scalive.Velora.dto.request.ProductRequestDTO;
import in.scalive.Velora.dto.request.ProductSearchFilterDTO;
import in.scalive.Velora.dto.request.UpdateProductRequestDTO;
import in.scalive.Velora.dto.response.PageResponseDTO;
import in.scalive.Velora.dto.response.ProductResponseAIDTO;
import in.scalive.Velora.dto.response.ProductResponseDTO;
import in.scalive.Velora.entity.Product;
import in.scalive.Velora.exception.DuplicateResourceException;
import in.scalive.Velora.exception.ResourceNotFoundException;
import in.scalive.Velora.repository.ProductRepository;
import in.scalive.Velora.service.ProductService;
@Service
@Transactional
public class ProductServiceImpl implements ProductService{
	private ProductRepository repo;
	private GeminiService geminiService;
 
	@Autowired
	public ProductServiceImpl(ProductRepository repo,GeminiService geminiService) {
		this.repo = repo;
		this.geminiService=geminiService;
	}
	
	private Product findProductById(Long id) {
		Optional<Product>opt=repo.findById(id);
		if(opt.isPresent()) {
			return opt.get();
		}
		throw new ResourceNotFoundException("Product", "id", id);
	}
	
	private Pageable createPageable(int page, int size, String sortBy,String sortDir) {
		Sort  sort;
		if(sortDir.equalsIgnoreCase("desc"))
			sort=Sort.by(sortBy).descending();
		else
			sort=Sort.by(sortBy).ascending();
		return PageRequest.of(page,size,sort);
	}
	
	private ProductResponseDTO mapToProductResponseDTO(Product product) {
		return ProductResponseDTO.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.stockQuantity(product.getStockQuantity())
				.category(product.getCategory())
				.brand(product.getBrand())
				.imageUrl(product.getImageUrl())
				.sku(product.getSku())
				.isAvailable(product.getIsAvailable())
				.inStock(product.getStockQuantity()>0)
				.averageRating(product.getAverageRating())
				.modelUrl(product.getModelUrl())
				.build();
	}
	
	private PageResponseDTO<ProductResponseDTO> mapToPageResponseDTO(Page<Product> page){
		List<ProductResponseDTO> products=new ArrayList<>();
		for(Product product:page.getContent()) {
			products.add(mapToProductResponseDTO(product));
		}
		       
		return PageResponseDTO.<ProductResponseDTO>builder()
				.content(products)
				.pageNumber(page.getNumber())
				.pageSize(page.getSize())
				.totalElements(page.getTotalElements())
				.totalPages(page.getTotalPages())
				.first(page.isFirst())
				.last(page.isLast())
				.hasNext(page.hasNext())
				.hasPrevious(page.hasPrevious())
				.build();
	}

	@Override
	public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
		if(productRequestDTO.getSku()!=null && repo.existsBySku(productRequestDTO.getSku()))
			throw new DuplicateResourceException("product", "sku", productRequestDTO.getSku()); 
		Product product = Product.builder()
				          .name(productRequestDTO.getName())
				          .description(productRequestDTO.getDescription())
				          .brand(productRequestDTO.getBrand())
				          .category(productRequestDTO.getCategory())
				          .imageUrl(productRequestDTO.getImageUrl())
				          .price(productRequestDTO.getPrice())
				          .sku(productRequestDTO.getSku())
				          .stockQuantity(productRequestDTO.getStockQuantity())
				          .modelUrl(productRequestDTO.getModelUrl())
				          .isAvailable(productRequestDTO.getIsAvailable()!=null?productRequestDTO.getIsAvailable():true)
				          .build();
		
		Product savedProduct=repo.save(product);
				          
		return mapToProductResponseDTO(savedProduct);
	}

	@Override
	public ProductResponseDTO getProductById(Long id) {
		Product product= findProductById(id);
		return mapToProductResponseDTO(product);
	}

	@Override
	public ProductResponseDTO getProductBySku(String sku) {
		Optional<Product> opt=repo.findBySku(sku);
		if(!opt.isPresent())
			throw new ResourceNotFoundException("product", "sku", sku);
		Product product=opt.get();
		return mapToProductResponseDTO(product);
	}

	@Override
	public List<ProductResponseDTO> getAllProducts() {
		List<Product>products=repo.findAll();
		List<ProductResponseDTO> responseList=new ArrayList<>(); 
		for(Product product: products) {
			responseList.add(mapToProductResponseDTO(product));
		}
		return responseList;
	}
	
	@Override
	public List<ProductResponseDTO> getLatestProducts() {
		List<Product>products=repo.findTop8ByOrderByIdDesc();
		List<ProductResponseDTO> responseList=new ArrayList<>(); 
		for(Product product: products) {
			responseList.add(mapToProductResponseDTO(product));
		}
		return responseList;
	}

	@Override
	public PageResponseDTO<ProductResponseDTO> getAllProductsPaginated(int page, int size, String sortBy,
			String sortDir) {
		Pageable pageable=createPageable(page, size, sortBy, sortDir);
		Page<Product>productPage=repo.findAll(pageable);
		return mapToPageResponseDTO(productPage);
	}

	@Override
	public List<ProductResponseDTO> getAvailableProducts() {
		List<Product>products=repo.findByIsAvailableTrue();
		List<ProductResponseDTO> responseList=new ArrayList<>(); 
		for(Product product: products) {
			responseList.add(mapToProductResponseDTO(product));
		}
		return responseList;
	}

	@Override
	public List<ProductResponseDTO> getProductsByCategory(String category) {
		List<Product>products=repo.findByCategoryIgnoreCase(category);
		List<ProductResponseDTO> responseList=new ArrayList<>(); 
		for(Product product: products) {
			responseList.add(mapToProductResponseDTO(product));
		}
		return responseList;
	}

	@Override
	public List<ProductResponseDTO> getProductsByPriceRange(double minPrice, double maxPrice) {
		List<Product>products=repo.findByPriceBetween(minPrice, maxPrice);
		List<ProductResponseDTO> responseList=new ArrayList<>(); 
		for(Product product: products) {
			responseList.add(mapToProductResponseDTO(product));
		}
		return responseList;
	}

	@Override
	public PageResponseDTO<ProductResponseDTO> searchProducts(String keyword, int page, int size) {
		Pageable pageable=PageRequest.of(page, size);
		Page<Product>products=repo.searchProducts(keyword,pageable);
		return mapToPageResponseDTO(products);
	}

	@Override
	public ProductResponseDTO updateProduct(Long id, UpdateProductRequestDTO updateProductRequestDTO) {
		Product product=findProductById(id);
		if(updateProductRequestDTO.getName()==null &&
				updateProductRequestDTO.getBrand()==null &&
				updateProductRequestDTO.getCategory()==null &&
				updateProductRequestDTO.getDescription()==null &&
				updateProductRequestDTO.getImageUrl()==null &&
				updateProductRequestDTO.getPrice()==null &&
				updateProductRequestDTO.getSku()==null &&
				updateProductRequestDTO.getStockQuantity()==null &&
				updateProductRequestDTO.getIsAvailable()==null &&
				updateProductRequestDTO.getModelUrl()==null) {
			throw new IllegalArgumentException("At least one field must be provided for updation");
		}
		if(updateProductRequestDTO.getName()!=null) {
			if(updateProductRequestDTO.getName().isBlank()) {
				throw new IllegalArgumentException("product name cannot be blank");
			}
			product.setName(updateProductRequestDTO.getName().trim());
		}
		if(updateProductRequestDTO.getDescription()!=null) {
			if(updateProductRequestDTO.getDescription().isBlank()) {
				throw new IllegalArgumentException("description cannot be left blank");
			}
			product.setDescription(updateProductRequestDTO.getDescription().trim());
		}
		if(updateProductRequestDTO.getModelUrl()!=null) {
			if(updateProductRequestDTO.getModelUrl().isBlank()) {
				throw new IllegalArgumentException("modelUrl cannot be left blank");
			}
			product.setModelUrl(updateProductRequestDTO.getModelUrl());
		}
		if(updateProductRequestDTO.getPrice()!=null) {
			product.setPrice(updateProductRequestDTO.getPrice());
		}
		if(updateProductRequestDTO.getStockQuantity()!=null) {
			product.setStockQuantity(updateProductRequestDTO.getStockQuantity());
		}
		if(updateProductRequestDTO.getCategory()!=null) {
			if(updateProductRequestDTO.getCategory().isBlank()) {
				throw new IllegalArgumentException("category cannot be blank");
			}
			product.setCategory(updateProductRequestDTO.getCategory().trim());
		}
		if(updateProductRequestDTO.getBrand()!=null) {
			if(updateProductRequestDTO.getBrand().isBlank()) {
				throw new IllegalArgumentException("brand name cannot be blank");
			}
			product.setBrand(updateProductRequestDTO.getBrand());
		}
		if(updateProductRequestDTO.getImageUrl()!=null) {
			if(updateProductRequestDTO.getImageUrl().isBlank()) {
				throw new IllegalArgumentException("Image URL cannot be blank");
			}
			product.setImageUrl(updateProductRequestDTO.getImageUrl().trim());
		}
		
		if(updateProductRequestDTO.getSku()!=null) {
			if(updateProductRequestDTO.getSku().isBlank()) {
				throw new IllegalArgumentException("sku cannot be blank");
			}
			String sku=updateProductRequestDTO.getSku();
			if(!sku.equals(product.getSku())) {
				if(repo.existsBySku(sku)) {
					throw new DuplicateResourceException("product","sku",sku);
				}
				product.setSku(updateProductRequestDTO.getSku());
			}
		}
		
		if(updateProductRequestDTO.getIsAvailable()!=null) {
			product.setIsAvailable(updateProductRequestDTO.getIsAvailable());
		}
		
		Product updatedProduct=repo.save(product);
		return mapToProductResponseDTO(updatedProduct);
	}

	@Override
	public void updateStock(Long productId, Integer quantity) {
		Product product=findProductById(productId);
		int newStock=product.getStockQuantity()+quantity;
		if(newStock<0)
			throw new IllegalArgumentException("stockQuantity cannot be negative, Current Stock: "+product.getStockQuantity());
		product.setStockQuantity(newStock);
		repo.save(product);
		
 	}

	@Override
	public List<ProductResponseDTO> getLowStockProducts(Integer threshold) {
		if(threshold<0)
			throw new IllegalArgumentException("threshold cannot be negative: "+threshold);
		List<Product> products=repo.findLowStockProdcuts(threshold);
		List<ProductResponseDTO> responseList=new ArrayList<>(); 
		for(Product product: products) {
			responseList.add(mapToProductResponseDTO(product));
		}
		return responseList;
	}

	@Override
	public boolean existsBySku(String sku) {	
		return repo.existsBySku(sku);
	}
	
	public ProductResponseAIDTO searchProductsByPrompt(String userPrompt) {
	    // Step 1: Gemini se filters extract karo
	    ProductSearchFilterDTO filter = geminiService.extractFilters(userPrompt);

	    // Step 2: Filters ke saath DB query maaro
	    List<Product>productList= repo.searchByFilters(
	        filter.getName(),
	        filter.getCategory(),
	        filter.getBrand(),
	        filter.getMinPrice(),
	        filter.getMaxPrice()
	    );
	    List<ProductResponseDTO>responseList=new ArrayList<>();
	    for(Product product:productList) {
	    	    responseList.add(mapToProductResponseDTO(product));
	    }
	    ProductResponseAIDTO response=ProductResponseAIDTO.builder()
	    		                          .dto(responseList)
	    		                          .message(filter.getMessage())
	    		                          .build();
	    return response;
	}
}
