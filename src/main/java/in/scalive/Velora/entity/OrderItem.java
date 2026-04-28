package in.scalive.Velora.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Table(name = "order_items", uniqueConstraints = @UniqueConstraint(columnNames = {"order_id","product_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "order_id",nullable = false)
   private Order order;
   
   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "product_id",nullable = false)
   private Product product;
   
   @Column(nullable = false,length = 200 )
   private String productName;
   
   @Column(length = 50)
   private String productSku;
   
   @Column(nullable = false)
   private Integer quantity;
   
   @Column(nullable = false)
   private Double unitPrice;
   
   @Column(nullable = false)
   private Double subTotal;
   
   @PrePersist
   public void calculateSubTotal() {
	   subTotal=unitPrice*quantity;
   }
   
   public static OrderItem fromCartItem(CartItem cartItem) {
	   return OrderItem.builder()
			   .product(cartItem.getProduct())
			   .productName(cartItem.getProduct().getName())
			   .productSku(cartItem.getProduct().getSku())
			   .quantity(cartItem.getQuantity())
			   .unitPrice(cartItem.getUnitPrice())
			   .subTotal(cartItem.getSubTotal())
			   .build();
   }
}
