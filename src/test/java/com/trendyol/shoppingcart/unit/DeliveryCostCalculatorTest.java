package com.trendyol.shoppingcart.unit;


import com.trendyol.shoppingcart.model.Category;
import com.trendyol.shoppingcart.model.Product;
import com.trendyol.shoppingcart.service.DeliveryCostCalculator;
import com.trendyol.shoppingcart.service.ShoppingCart;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeliveryCostCalculatorTest {

    @Test
    public void test_deliveryCostCalculatorWith2CategoriesAnd5Products() {
        // categories
        Category food = new Category("Food");
        Category technology = new Category("Technology");

        // products
        Product apple = new Product("Apple", 10.0, food);
        Product almond = new Product("Almonds", 15.0, food);

        Product jean = new Product("Jean", 100, technology);
        Product hat = new Product("Hat", 15, technology);
        Product shirt = new Product("Shirt", 50, technology);

        // 2 categories and 5 products
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.addItem(apple, 2);
        shoppingCart.addItem(almond, 2);
        shoppingCart.addItem(jean, 2);
        shoppingCart.addItem(hat, 2);
        shoppingCart.addItem(shirt, 2);

        DeliveryCostCalculator deliveryCostCalculator = new DeliveryCostCalculator(1, 0.5, 2.99);

        // expected delivery cost is (1*2) + (0.5*5) + 2.99 = 7.49
        double deliveryCost = deliveryCostCalculator.calculateFor(shoppingCart);
        assertEquals(deliveryCost, 7.49, 0);
    }

    @Test
    public void test_deliveryCostCalculatorSingleCategoryThreeProducts() {
        // categories
        Category food = new Category("Food");

        // products
        Product apple = new Product("Apple", 10.0, food);
        Product almond = new Product("Almonds", 15.0, food);
        Product lemon = new Product("Lemon", 9.99, food);

        // 1 category and 3 products
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.addItem(apple, 2);
        shoppingCart.addItem(almond, 2);
        shoppingCart.addItem(lemon, 5);

        DeliveryCostCalculator deliveryCostCalculator = new DeliveryCostCalculator(1, 2.5, 1.99);

        // expected delivery cost is (1*1) + (2.5*3) + 1.99 = 10.49
        double deliveryCost = deliveryCostCalculator.calculateFor(shoppingCart);
        assertEquals(deliveryCost, 10.49, 0);
    }


}
