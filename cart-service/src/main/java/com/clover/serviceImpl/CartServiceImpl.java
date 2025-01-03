package com.clover.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.clover.feignCleint.InventoryClient;
import com.clover.model.Cart;
import com.clover.model.CartItem;
import com.clover.model.CartItemDeleteRequest;
import com.clover.model.CartRequest;
import com.clover.model.Inventory;
import com.clover.repository.CartRepository;
import com.clover.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

	@Autowired
	private InventoryClient inventoryClient;

	public static String idGenerator() {
		String idGen = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		return idGen;
	}

	@Override
	public boolean addToCart(String customerId, @RequestBody CartRequest cartRequest) {
		try {
			logger.info("CartServiceImpl:addToCart execution started");

			if (customerId == null || customerId.isEmpty()) {
				logger.error("CartServiceImpl: Invalid input data. CustomerId must be valid.");
				return false;
			}

			if (cartRequest == null || cartRequest.getItems() == null || cartRequest.getItems().isEmpty()) {
				logger.error("CartServiceImpl: Invalid items in the cart request for CustomerId: {}", customerId);
				return false;
			}

			Optional<Cart> optionalCart = cartRepository.findByCustomerId(customerId);
			Cart cart;

			if (optionalCart.isPresent()) {
				cart = optionalCart.get();
			} else {
				cart = new Cart();
				cart.setItems(new ArrayList<>());
				String uniqueCartId = generateUniqueId("CRT", id -> cartRepository.findById(id).isEmpty());
				cart.setCartId(uniqueCartId);
				cart.setCustomerId(customerId);
				cart.setTotalPriceOfCart(0.0);
				cart.setPaymentStatus("PENDING");
				cart.setCreatedAt(new Date());
				cart.setIsActive(true);
			}

			double totalItemPrice = 0.0;
			List<String> insufficientProducts = new ArrayList<>();

			for (CartItem cartItemRequest : cartRequest.getItems()) {
				if (cartItemRequest.getQuantity() == null || cartItemRequest.getQuantity() <= 0) {
					logger.error("CartServiceImpl: Invalid quantity: {} for ProductId: {}",
							cartItemRequest.getQuantity(), cartItemRequest.getCartProductId());
					continue;
				}

				String productId = cartItemRequest.getCartProductId();
				String cartInventoryId = cartItemRequest.getCartInventoryId();
				logger.info("CartServiceImpl: Fetching inventory details for Product ID: {} and InventoryId: {}",
						productId, cartInventoryId);

				List<Inventory> inventoryList = inventoryClient.getInventoryByProductId(productId);

				if (inventoryList == null || inventoryList.isEmpty()) {
					logger.error("CartServiceImpl: Inventory details not found for Product ID: {}", productId);
					continue;
				}

				int remainingQuantityToDeduct = cartItemRequest.getQuantity();
				boolean inventorySufficient = false;
				Inventory selectedInventory = null;

				for (Inventory inventory : inventoryList) {
					if (cartInventoryId != null && !cartInventoryId.equals(inventory.getInventoryId())) {
						continue;
					}

					if (cartInventoryId != null && cartInventoryId.equals(inventory.getInventoryId())) {
						if (remainingQuantityToDeduct > inventory.getQuantity()) {
							insufficientProducts.add("Product ID: " + productId + ", Inventory ID: " + cartInventoryId);
						} else {
							int updatedInventoryQuantity = inventory.getQuantity() - remainingQuantityToDeduct;
							inventoryClient.updateQuantityByProductIdAndInventoryId(productId,
									inventory.getInventoryId(), updatedInventoryQuantity);

							remainingQuantityToDeduct = 0;
							inventorySufficient = true;

							selectedInventory = inventory;

							logger.info(
									"CartServiceImpl: Inventory updated - ProductId: {}, InventoryId: {}, Remaining Quantity: {}",
									productId, inventory.getInventoryId(), updatedInventoryQuantity);
						}
					}
				}

				if (remainingQuantityToDeduct > 0) {
					logger.error(
							"CartServiceImpl: Unable to fulfill the full quantity for ProductId: {} and InventoryId: {}. "
									+ "Requested: {}, Fulfilled: {}",
							productId, cartInventoryId, cartItemRequest.getQuantity(),
							cartItemRequest.getQuantity() - remainingQuantityToDeduct);
					continue;
				}

				CartItem cartItem = new CartItem();
				String uniqueCartItemId = generateUniqueId("CRI",
						id -> cart.getItems().stream().noneMatch(item -> item.getCartItemId().equals(id)));
				cartItem.setCartItemId(uniqueCartItemId);
				cartItem.setCartProductId(productId);
				cartItem.setQuantity(cartItemRequest.getQuantity());

				if (selectedInventory != null) {
					cartItem.setPriceOfCartItem(selectedInventory.getPrice());
					cartItem.setCartProductName(selectedInventory.getInventoryProductName());
				}

				double itemTotalPrice = cartItemRequest.getQuantity() * cartItem.getPriceOfCartItem();
				cartItem.setTotalPriceOfCartItem(itemTotalPrice);
				totalItemPrice += itemTotalPrice;

				cartItem.setCartInventoryId(cartInventoryId);

				cart.getItems().add(cartItem);
				cartItem.setIsActive(true);
			}

			double updatedTotalPrice = (cart.getTotalPriceOfCart() == null ? 0.0 : cart.getTotalPriceOfCart())
					+ totalItemPrice;
			cart.setTotalPriceOfCart(updatedTotalPrice);

			cartRepository.save(cart);

			logger.info("CartServiceImpl:addToCart execution completed successfully");
			return true;

		} catch (Exception e) {
			logger.error("CartServiceImpl:addToCart: Something went wrong", e);
			return false;
		}
	}

	private String generateUniqueId(String prefix, Predicate<String> isUnique) {
		boolean isUniqueIdFound = false;
		String uniqueId = "";
		while (!isUniqueIdFound) {
			uniqueId = prefix + CartServiceImpl.idGenerator();
			if (isUnique.test(uniqueId)) {
				isUniqueIdFound = true;
			}
		}
		return uniqueId;
	}

	@Override
	public boolean updateCartByOrderService(String customerId, String cartProductId, String cartInventoryId) {
		Optional<Cart> optionalCart = cartRepository.findByCustomerId(customerId);

		if (!optionalCart.isPresent()) {
			logger.error("No cart found for customerId={}", customerId);
			return false;
		}

		Cart cart = optionalCart.get();
		System.err.println(cart.getCustomerId());
		boolean updated = false;

		for (CartItem cartItem : cart.getItems()) {
			if (cartItem.getCartProductId().equals(cartProductId)
					&& cartItem.getCartInventoryId().equals(cartInventoryId)) {
				cartItem.setOrderStatus("ORDERED on " + new Date());
				cartItem.setIsActive(false);
				cartItem.setModifiedAt(new Date());
				updated = true;
				break;
			}
		}

		if (updated) {
			cartRepository.save(cart);
		} else {
			logger.error("Cart item not found for cartProductId={} and cartInventoryId={}", cartProductId,
					cartInventoryId);
		}

		return updated;
	}

	@Override
	public boolean updateCart(String customerId, CartRequest cartRequest) {
		try {
			logger.info("CartServiceImpl:updateCart execution started");

			if (customerId == null || customerId.isEmpty()) {
				logger.error("CartServiceImpl: Invalid input data. CustomerId must be valid.");
				return false;
			}

			if (cartRequest == null || cartRequest.getItems() == null || cartRequest.getItems().isEmpty()) {
				logger.error("CartServiceImpl: Invalid items in the cart request for CustomerId: {}", customerId);
				return false;
			}

			Optional<Cart> optionalCart = cartRepository.findByCustomerId(customerId);
			if (!optionalCart.isPresent()) {
				logger.error("CartServiceImpl: No cart found for CustomerId: {}", customerId);
				return false;
			}
			Cart cart = optionalCart.get();

			double totalItemPrice = 0.0;

			for (CartItem cartItemRequest : cartRequest.getItems()) {

				if (cartItemRequest.getQuantity() == null || cartItemRequest.getQuantity() <= 0) {
					logger.error("CartServiceImpl: Invalid quantity: {} for ProductId: {}",
							cartItemRequest.getQuantity(), cartItemRequest.getCartProductId());
					continue;
				}

				String productId = cartItemRequest.getCartProductId();
				String cartInventoryId = cartItemRequest.getCartInventoryId();
				logger.info("CartServiceImpl: Updating inventory details for Product ID: {} and InventoryId: {}",
						productId, cartInventoryId);

				List<Inventory> inventoryList = inventoryClient.getInventoryByProductId(productId);
				if (inventoryList == null || inventoryList.isEmpty()) {
					logger.error("CartServiceImpl: Inventory details not found for Product ID: {}", productId);
					continue;
				}

				CartItem cartItem = findCartItem(cart, productId, cartInventoryId);
				if (cartItem == null) {
					logger.error("CartServiceImpl: Cart item with ProductId {} and InventoryId {} not found.",
							productId, cartInventoryId);
					continue;
				}

				int updatedQuantity = cartItemRequest.getQuantity();
				int currentQuantity = cartItem.getQuantity();
				if (updatedQuantity != currentQuantity) {
					int remainingQuantityToDeduct = updatedQuantity - currentQuantity;

					double itemTotalPrice = 0.0;
					boolean inventoryUpdated = false;
					boolean matchedInventoryFound = false;
					Inventory selectedInventory = null;

					for (Inventory inventory : inventoryList) {
						if (cartInventoryId != null && !cartInventoryId.equals(inventory.getInventoryId())) {
							logger.warn(
									"CartServiceImpl: InventoryId {} does not match with cartInventoryId {}. Skipping this inventory.",
									inventory.getInventoryId(), cartInventoryId);
							continue;
						}

						if (cartInventoryId != null && cartInventoryId.equals(inventory.getInventoryId())) {
							matchedInventoryFound = true;

							int availableQuantity = inventory.getQuantity();
							if (availableQuantity >= remainingQuantityToDeduct) {
								int updatedInventoryQuantity = availableQuantity - remainingQuantityToDeduct;
								inventoryClient.updateQuantityByProductIdAndInventoryId(productId,
										inventory.getInventoryId(), updatedInventoryQuantity);

								remainingQuantityToDeduct = 0;

								itemTotalPrice += remainingQuantityToDeduct * inventory.getPrice();
								cartItem.setQuantity(updatedQuantity);
								cartItem.setPriceOfCartItem(inventory.getPrice());
								cartItem.setTotalPriceOfCartItem(updatedQuantity * inventory.getPrice());

								selectedInventory = inventory;

								inventoryUpdated = true;
							} else {
								logger.error(
										"CartServiceImpl: Insufficient stock for ProductId {} and InventoryId {}. Requested: {}, Available: {}",
										productId, cartInventoryId, remainingQuantityToDeduct, availableQuantity);
							}
						}
					}

					if (!matchedInventoryFound) {
						logger.error(
								"CartServiceImpl: No matching InventoryId found for ProductId {} and InventoryId {}. Skipping item update.",
								productId, cartInventoryId);
						continue;
					}

					if (remainingQuantityToDeduct > 0) {
						logger.error(
								"CartServiceImpl: Unable to fulfill the updated quantity for ProductId: {} and InventoryId: {}. Requested: {}, Fulfilled: {}",
								productId, cartInventoryId, updatedQuantity,
								updatedQuantity - remainingQuantityToDeduct);
						continue;
					}

					if (selectedInventory != null) {
						cartItem.setPriceOfCartItem(selectedInventory.getPrice());
						cartItem.setCartProductName(selectedInventory.getInventoryProductName());
					}
				}

				totalItemPrice += cartItem.getTotalPriceOfCartItem();
			}

			double updatedTotalPrice = (cart.getTotalPriceOfCart() == null ? 0.0 : cart.getTotalPriceOfCart())
					+ totalItemPrice;
			cart.setTotalPriceOfCart(updatedTotalPrice);

			cartRepository.save(cart);

			logger.info("CartServiceImpl:updateCart execution completed successfully");
			return true;

		} catch (Exception e) {
			logger.error("CartServiceImpl:updateCart: Something went wrong", e);
			return false;
		}
	}

	private CartItem findCartItem(Cart cart, String productId, String cartInventoryId) {
		for (CartItem item : cart.getItems()) {
			if (item.getCartProductId().equals(productId) && item.getCartInventoryId().equals(cartInventoryId)) {
				return item;
			}
		}
		return null;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean deleteCartByCustomerId(String customerId, CartItemDeleteRequest cartItemDeleteRequest) {
		try {
			logger.info(
					"CartServiceImpl: softDeleteCartItem execution started for CustomerId: {}, ProductId: {}, InventoryId: {}",
					customerId, cartItemDeleteRequest.getCartProductId(), cartItemDeleteRequest.getCartInventoryId());

			Optional<Cart> optionalCart = cartRepository.findByCustomerId(customerId);
			if (!optionalCart.isPresent()) {
				logger.error("CartServiceImpl: No cart found for CustomerId: {}", customerId);
				return false;
			}
			Cart cart = optionalCart.get();

			CartItem cartItem = findCartItem(cart, cartItemDeleteRequest.getCartProductId(),
					cartItemDeleteRequest.getCartInventoryId());
			if (cartItem == null) {
				logger.error(
						"CartServiceImpl: Cart item not found for ProductId: {} and InventoryId: {} for CustomerId: {}",
						cartItemDeleteRequest.getCartProductId(), cartItemDeleteRequest.getCartInventoryId(),
						customerId);
				return false;
			}

			cartItem.setIsActive(false);
			cartItem.setModifiedAt(new Date());
			cartRepository.save(cart);

			// restore the quantity of the cart item back to the inventory
			List<Inventory> inventoryList = inventoryClient
					.getInventoryByProductId(cartItemDeleteRequest.getCartProductId());
			boolean inventoryUpdated = false;

			for (Inventory inventory : inventoryList) {
				if (cartItemDeleteRequest.getCartInventoryId().equals(inventory.getInventoryId())) {
					int updatedQuantity = inventory.getQuantity() + cartItem.getQuantity();
					inventoryClient.updateQuantityByProductIdAndInventoryId(cartItemDeleteRequest.getCartProductId(),
							cartItemDeleteRequest.getCartInventoryId(), updatedQuantity);

					logger.info("Inventory updated for ProductId: {}, InventoryId: {}. New Quantity: {}",
							cartItemDeleteRequest.getCartProductId(), cartItemDeleteRequest.getCartInventoryId(),
							updatedQuantity);

					inventoryUpdated = true;
					break;
				}
			}

			if (!inventoryUpdated) {
				logger.error("CartServiceImpl: No matching InventoryId found for ProductId: {} and InventoryId: {}.",
						cartItemDeleteRequest.getCartProductId(), cartItemDeleteRequest.getCartInventoryId());
				return false;
			}

			// Recalculate total cart price after soft delete
			double updatedTotalPrice = cart.getItems().stream().filter(CartItem::getIsActive)

					.mapToDouble(item -> item.getQuantity() * item.getPriceOfCartItem())

					.sum();

			cart.setTotalPriceOfCart(updatedTotalPrice);
			cartRepository.save(cart);

			// Check if the cart has any active items left. If no active items, soft delete
			// the cart itself.
			boolean hasActiveItems = cart.getItems().stream().anyMatch(CartItem::getIsActive);
			if (!hasActiveItems) {
				cart.setIsActive(false);
				cartRepository.save(cart);
				logger.info("Cart soft-deleted as there are no active items remaining.");
			}

			logger.info(
					"CartServiceImpl: softDeleteCartItem executed successfully for CustomerId: {}, ProductId: {}, InventoryId: {}",
					customerId, cartItemDeleteRequest.getCartProductId(), cartItemDeleteRequest.getCartInventoryId());

			return true;

		} catch (Exception e) {
			logger.error(
					"CartServiceImpl: softDeleteCartItem failed for CustomerId: {}, ProductId: {}, InventoryId: {}",
					customerId, cartItemDeleteRequest.getCartProductId(), cartItemDeleteRequest.getCartInventoryId(),
					e);
			return false;
		}
	}

	@Override
	public List<Cart> getAllActiveCartsByCustomerId(String customerId) {
		try {
			logger.info("CartServiceImpl:getAllActiveCartsByCustomerId execution started for CustomerId: {}",
					customerId);

			List<Cart> activeCarts = cartRepository.findByCustomerIdAndIsActive(customerId, true);

			if (activeCarts == null || activeCarts.isEmpty()) {
				logger.info("CartServiceImpl: No active carts found for CustomerId: {}", customerId);
			} else {
				logger.info("CartServiceImpl: Found {} active carts for CustomerId: {}", customerId,
						activeCarts.size());
			}

			return activeCarts;

		} catch (Exception e) {
			logger.error("CartServiceImpl:getAllActiveCartsByCustomerId: Something went wrong", e);
			return Collections.emptyList();
		}
	}

}
