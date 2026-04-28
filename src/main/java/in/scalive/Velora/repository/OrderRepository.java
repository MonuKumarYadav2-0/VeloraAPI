package in.scalive.Velora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.scalive.Velora.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
	//Return List of orders of a specific user in desc order of date
       List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    
       //return orders of a specific user based on pagination
       Page<Order> findByUserId(Long userId, Pageable pageable);
     
       //list of orders based on status
       List<Order> findByStatus(String status);
       
       //return order with items on the basis of order_id
       @Query("SELECT o FROM Order o "+
               "LEFT JOIN FETCH o.orderItems oi "+
       		    "LEFT JOIN FETCH oi.product "+
               "WHERE o.id=:orderId"
       		 )
       Optional<Order> findByIdWithItems( @Param("orderId") Long orderId);
       
     //return order with items on the basis of orderNumber
       @Query("SELECT o FROM Order o "+
               "LEFT JOIN FETCH o.orderItems oi "+
       		    "LEFT JOIN FETCH oi.product "+
               "WHERE o.orderNumber=:orderNumber"
       		 )
       Optional<Order> findByOrderNumber( @Param("orderNumber") String orderNumber);
       
       @Query("SELECT o FROM Order o WHERE (LOWER(o.user.fullName) LIKE LOWER(CONCAT('%',:keyword,'%'))"+
    		      "OR LOWER(o.user.email) LIKE LOWER(CONCAT('%',:keyword,'%'))" +
    		      "OR o.orderNumber LIKE %:keyword%)")
    		     public Page<Order> searchByOrders(@Param("keyword") String keyword,Pageable pageable);

       @Query("""
    	        SELECT DISTINCT o FROM Order o
    	        JOIN FETCH o.orderItems oi
    	        JOIN FETCH oi.product
    	        WHERE o.user.id = :userId
    	    """)
	   List<Order> findByUserId(@Param("userId") Long userId);
       
       Optional<Order> findByRazorpayOrderId(String razorpayOrderId);
}
