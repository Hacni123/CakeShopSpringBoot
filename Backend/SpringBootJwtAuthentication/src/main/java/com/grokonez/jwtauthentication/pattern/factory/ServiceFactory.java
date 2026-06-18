package com.grokonez.jwtauthentication.pattern.factory;

/**
 * Service Factory - Implements Factory Design Pattern
 * Creates instances of different service types
 * Centralizes object creation and provides loose coupling
 */
public enum ServiceFactory {
    CAKE_SERVICE("cakeService"),
    ORDER_SERVICE("orderService"),
    CATEGORY_SERVICE("categoryService"),
    CART_SERVICE("cartService"),
    USER_SERVICE("userService");

    private String serviceName;

    ServiceFactory(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    /**
     * Get service by name
     */
    public static ServiceFactory getByName(String name) {
        for (ServiceFactory factory : ServiceFactory.values()) {
            if (factory.serviceName.equalsIgnoreCase(name)) {
                return factory;
            }
        }
        throw new IllegalArgumentException("Unknown service: " + name);
    }
}

