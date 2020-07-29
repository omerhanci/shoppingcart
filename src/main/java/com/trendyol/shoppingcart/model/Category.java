package com.trendyol.shoppingcart.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Category {
    private String title;
    private Category parent;

    public Category(String title) {
        this.title = title;
    }
}
