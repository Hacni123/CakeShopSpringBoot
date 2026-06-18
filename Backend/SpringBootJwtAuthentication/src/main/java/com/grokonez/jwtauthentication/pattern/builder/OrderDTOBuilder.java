package com.grokonez.jwtauthentication.pattern.builder;

import com.grokonez.jwtauthentication.dto.OrderDTO;
import java.util.HashSet;
import java.util.Set;

/**
 * Builder Pattern for OrderDTO
 * Simplifies creation of complex OrderDTO objects
 * Provides fluent API for object construction
 */
public class OrderDTOBuilder {

    private Long orderid;
    private Long userId;
    private Long cartId;
    private String status;
    private Set<Long> cakeIds;
    private Double totalPrice;
    private String deliveryAddress;
    private String paymentMethod;

    public OrderDTOBuilder() {
        this.cakeIds = new HashSet<>();
    }

    public OrderDTOBuilder withOrderId(Long orderid) {
        this.orderid = orderid;
        return this;
    }

    public OrderDTOBuilder withUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public OrderDTOBuilder withCartId(Long cartId) {
        this.cartId = cartId;
        return this;
    }

    public OrderDTOBuilder withStatus(String status) {
        this.status = status;
        return this;
    }

    public OrderDTOBuilder withCakeId(Long cakeId) {
        this.cakeIds.add(cakeId);
        return this;
    }

    public OrderDTOBuilder withCakeIds(Set<Long> cakeIds) {
        this.cakeIds = cakeIds;
        return this;
    }

    public OrderDTOBuilder withTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public OrderDTOBuilder withDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public OrderDTOBuilder withPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    /**
     * Build the OrderDTO object
     */
    public OrderDTO build() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderid(this.orderid);
        orderDTO.setStatus(this.status);
        return orderDTO;
    }

    /**
     * Reset builder for reuse
     */
    public void reset() {
        this.orderid = null;
        this.userId = null;
        this.cartId = null;
        this.status = null;
        this.cakeIds.clear();
        this.totalPrice = null;
        this.deliveryAddress = null;
        this.paymentMethod = null;
    }
}

