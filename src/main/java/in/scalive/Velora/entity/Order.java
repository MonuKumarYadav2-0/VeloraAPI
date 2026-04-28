package in.scalive.Velora.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(nullable = false,unique = true, length = 50)
    private String orderNumber;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems=new ArrayList<>();
    
    @Column(nullable = false)
    private Double totalAmount;
    
    public final static String STATUS_CONFIRMED="CONFIRMED";
    public final static String STATUS_CANCELLED="CANCELLED";
    public final static String STATUS_PENDING = "PENDING";
    public final static String STATUS_FAILED = "FAILED";
    
    
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status=STATUS_PENDING;
    
  
    
    
    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    @Column(name = "razorpay_signature")
    private String razorpaySignature;
    
    
    
    @Column
    private String notes;
    
    @Column(nullable = false)
    private LocalDateTime orderDate;
	
	@PrePersist
	public void generateOrderNumber() {
		if(this.orderNumber==null) {
		   this.orderNumber="ORD-"+UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		}
		if(this.orderDate==null) {
			this.orderDate=LocalDateTime.now();
		}
	}
	
	public void addOrderItem(OrderItem item) {
		this.orderItems.add(item);
		item.setOrder(this);
	}
	
	public void calculateTotals() {
		Double amount=0.0;
		for(OrderItem item:orderItems) {
			amount+=item.getSubTotal();
		}
		this.totalAmount=amount;
	}

}
