package in.scalive.Velora.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.scalive.Velora.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{

}
