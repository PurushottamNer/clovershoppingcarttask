package com.clover.ServiceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clover.feignCleint.CartClient;
import com.clover.feignCleint.InventoryCleint;
import com.clover.model.Cart;
import com.clover.model.CartItem;
import com.clover.model.Order1;
import com.clover.model.OrderItem;
import com.clover.repository.OrderRepository;
import com.clover.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CartClient cartClient;

	@Autowired
	private InventoryCleint inventoryClient;

//	@Autowired
//	private PaymentClient paymentClient;

	public static String idGenerator() {
		String idGen = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		return idGen;
	}

	@Override
	public boolean createOrder(String customerId, Order1 orderRequest) {
		if (customerId == null || orderRequest == null || orderRequest.getOrderItems() == null
				|| orderRequest.getOrderItems().isEmpty()) {
			return false;
		}

		boolean isUniqueIdFound = false;
		String uniqueId = "";
		while (!isUniqueIdFound) {
			uniqueId = "ORD" + OrderServiceImpl.idGenerator();
			if (orderRepository.findById(uniqueId).isEmpty()) {
				isUniqueIdFound = true;
			}
		}

		double totalAmount = 0.0;
		List<OrderItem> orderItems = new ArrayList<>();

		for (OrderItem orderItem : orderRequest.getOrderItems()) {
			List<Cart> listOfCarts = cartClient.getAllActiveCartsByCustomerId(customerId);

			for (Cart cart : listOfCarts) {
				for (CartItem cartItem : cart.getItems()) {
					boolean isUniqueIdFound1 = false;
					String uniqueId1 = "";
					while (!isUniqueIdFound1) {
						uniqueId1 = "ORI" + OrderServiceImpl.idGenerator();
						if (orderRepository.findById(uniqueId1).isEmpty()) {
							isUniqueIdFound1 = true;
						}
					}

					OrderItem orderItem1 = new OrderItem();
					orderItem1.setOrderItemId(uniqueId1);
					orderItem1.setCartItemId(cartItem.getCartItemId());
					orderItem1.setProductId(cartItem.getCartProductId());
					orderItem1.setProductName(cartItem.getCartProductName());
					orderItem1.setQuantity(cartItem.getQuantity());
					orderItem1.setInventoryId(cartItem.getCartInventoryId());
					orderItem1.setUnitPrice(cartItem.getPriceOfCartItem());
					orderItem1.setTotalPrice(cartItem.getTotalPriceOfCartItem());
					orderItem1.setCreatedAt(new Date());
					orderItem1.setIsActive(true);
					orderItems.add(orderItem1);

					totalAmount += cartItem.getTotalPriceOfCartItem();

					cartClient.updateCartByOrderService(customerId, cartItem.getCartProductId(),
							cartItem.getCartInventoryId());

				}
			}
		}

		Order1 order = new Order1();
		order.setOrderId(uniqueId);
		order.setCustomerId(customerId);
		order.setOrderItems(orderItems);
		order.setTotalAmount(totalAmount);
		order.setPaymentStatus("PENDING");
		order.setOrderStatus("PENDING");
		order.setCreatedAt(new Date());
		order.setShippingAddress(orderRequest.getShippingAddress());
		order.setPaymentMethod(orderRequest.getPaymentMethod());
		order.setTrackingNumber(orderRequest.getTrackingNumber());
		order.setDeliveryDate(new Date());
		order.setIsActive(true);

		orderRepository.save(order);

		return true;
	}

}
