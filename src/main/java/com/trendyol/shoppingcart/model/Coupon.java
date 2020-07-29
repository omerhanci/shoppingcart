package com.trendyol.shoppingcart.model;
import com.trendyol.shoppingcart.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Coupon {
    private int minAmount;
    private int discountAmount;
    private DiscountType discountType;
}
