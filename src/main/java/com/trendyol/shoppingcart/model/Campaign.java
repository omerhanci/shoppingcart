package com.trendyol.shoppingcart.model;

import com.trendyol.shoppingcart.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Campaign {
    private Category category;
    private double rate;
    private int limit;
    private DiscountType discountType;
}
