package com.trendyol.shoppingcart.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeliveryCostCalculator {
    private double costPerDelivery;
    private double costPerProduct;
    private double fixedCost;

    /**
     *
     * @param cart
     * @return Applies a certain formula on shopping cart to get delivery cost
     * Formula is : (cost per delivery * Number of deliveries) + (cost per product * number of products) + fixed cost
     */
    public double calculateFor(ShoppingCart cart) {
        int numberOfDeliveries = cart.getNumberOfDeliveries();
        int numberOfProducts = cart.getNumberOfProducts();
        double deliveryCost = (costPerDelivery*numberOfDeliveries) + (costPerProduct*numberOfProducts) + fixedCost;
        cart.setDeliveryCost(deliveryCost);
        return deliveryCost;
    }
}
