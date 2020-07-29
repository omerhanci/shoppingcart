package com.trendyol.shoppingcart.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ProductQuantityMap {
    private Map<Product, Integer> productQuantityMap;
}
