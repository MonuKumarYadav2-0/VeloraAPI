package in.scalive.Velora.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.grammars.hql.HqlParser.NthSideClauseContext;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
	  @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
	  
	  @OneToOne(fetch = FetchType.LAZY)
	  @JoinColumn(name = "user_id",nullable = false,unique = true)
      private User user;
	  
	  @OneToMany(fetch = FetchType.EAGER,mappedBy = "cart",cascade = CascadeType.ALL,orphanRemoval = true)
	  @Builder.Default
      private List<CartItem> cartItems=new ArrayList<>();
	  
	  @Column
	  @Builder.Default
      private Double totalAmount=0.0;
	  
	  @Column(nullable = false)
	  @Builder.Default
      private Integer totalItems=0;
	  
	  @CreationTimestamp
	  @Column(updatable = false)
      private LocalDateTime createdAt;
	  
	  @UpdateTimestamp
      private LocalDateTime updatedAt;
	  
	  //Helper Methods
	  public void addCartItem(CartItem item) {
		  cartItems.add(item);
		  item.setCart(this);
		  this.recalulateTotals();
	  }
	  public void removeCartItem(CartItem item) {
		  cartItems.remove(item);
		  item.setCart(null);
		  this.recalulateTotals();
	  }
	  public void recalulateTotals() {
		  Integer totalItemCount=0;
		  for(CartItem item: cartItems) {
			  totalItemCount+=item.getQuantity();
		}
		  this.totalItems=totalItemCount;
		  
		  Double total=0.0;
		  for(CartItem item: cartItems) {
			  total+=item.getSubTotal();
		  }
		  this.totalAmount=total;
	  }
	  
	  public void clearCart() {
		  //making copy of cartItems to prevent original cartItems list from dirty on write
		  for(CartItem item:new ArrayList<>(cartItems)) {
			  item.setCart(null);
		  }
		  this.cartItems.clear();
		  this.totalAmount=0.0;
		  this.totalItems=0;
	  }
      
}
