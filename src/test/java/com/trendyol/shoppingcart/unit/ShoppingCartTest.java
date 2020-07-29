package com.trendyol.shoppingcart.unit;

import com.trendyol.shoppingcart.enums.DiscountType;
import com.trendyol.shoppingcart.model.Campaign;
import com.trendyol.shoppingcart.model.Category;
import com.trendyol.shoppingcart.model.Coupon;
import com.trendyol.shoppingcart.model.Product;
import com.trendyol.shoppingcart.service.ShoppingCart;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShoppingCartTest {


    @Test
    public void test_addItemSingleCategoryThreeProducts() {
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

        assertEquals(shoppingCart.getNumberOfDeliveries(),1);
        assertEquals(shoppingCart.getNumberOfProducts(),3);
    }

    @Test
    public void test_addItemMultipleCategoryMultipleProducts() {
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

        assertEquals(shoppingCart.getNumberOfDeliveries(),2);
        assertEquals(shoppingCart.getNumberOfProducts(),5);
    }

    @Test
    public void test_addSameProductTwice() {
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
        shoppingCart.addItem(apple, 5);

        assertEquals(shoppingCart.getNumberOfDeliveries(),2);
        assertEquals(shoppingCart.getNumberOfProducts(),5);

        assertEquals(shoppingCart.getCategoryProductQuantityMap().get(food).getProductQuantityMap().get(apple), java.util.Optional.of(7).get());
    }

    @Test
    public void test_applySingleDiscount() {
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
        shoppingCart.addItem(apple, 2); // 20
        shoppingCart.addItem(almond, 2); // 30
        shoppingCart.addItem(jean, 2);
        shoppingCart.addItem(hat, 2);
        shoppingCart.addItem(shirt, 2);
        shoppingCart.addItem(apple, 5); // 50

        // total cost = 100
        Campaign campaign = new Campaign(food, 20.0, 4, DiscountType.Rate);

        //campaign should be 20
        shoppingCart.applyDiscounts(campaign);

        double campaignDiscount = shoppingCart.getCampaignDiscount();
        assertEquals(campaignDiscount, 20.0, 0);
    }

    @Test
    public void test_applySingleDiscountLimit() {
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
        shoppingCart.addItem(apple, 2); // 20
        shoppingCart.addItem(almond, 2); // 30
        shoppingCart.addItem(jean, 2);
        shoppingCart.addItem(hat, 2);
        shoppingCart.addItem(shirt, 2);
        shoppingCart.addItem(apple, 5); // 50

        // total cost = 100
        Campaign campaign = new Campaign(food, 20.0, 10, DiscountType.Rate);

        //campaign should be 0 since we cannot pass the limit
        shoppingCart.applyDiscounts(campaign);

        double campaignDiscount = shoppingCart.getCampaignDiscount();
        assertEquals(campaignDiscount, 0, 0);
    }

    @Test
    public void test_applyTwoDiscountsWithOneInvalid() {
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
        shoppingCart.addItem(apple, 2); // 20
        shoppingCart.addItem(almond, 2); // 30
        shoppingCart.addItem(apple, 5); // 50

        shoppingCart.addItem(jean, 2); // 200
        shoppingCart.addItem(hat, 2); // 30
        shoppingCart.addItem(shirt, 2); // 100

        // total cost = 100 for food
        Campaign foodCampaign = new Campaign(food, 20.0, 4, DiscountType.Rate);

        // this campaign should not be applied since it is not over the limit
        Campaign techCampaign = new Campaign(technology, 25.0, 8, DiscountType.Rate);

        //campaign should be 20
        shoppingCart.applyDiscounts(foodCampaign, techCampaign);

        double campaignDiscount = shoppingCart.getCampaignDiscount();
        assertEquals(campaignDiscount, 20.0, 0);
    }

    @Test
    public void test_applyTwoDiscounts() {
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
        shoppingCart.addItem(apple, 2); // 20
        shoppingCart.addItem(almond, 2); // 30
        shoppingCart.addItem(apple, 5); // 50

        shoppingCart.addItem(jean, 2); // 200
        shoppingCart.addItem(hat, 2); // 30
        shoppingCart.addItem(shirt, 2); // 100

        // total cost = 100 for food
        Campaign foodCampaign = new Campaign(food, 20.0, 4, DiscountType.Rate);

        // this campaign should  be applied since it is over the limit and better discount
        Campaign techCampaign = new Campaign(technology, 25.0, 6, DiscountType.Rate);

        // total discount shoul be 330 * 25/100 = 82.5
        shoppingCart.applyDiscounts(foodCampaign, techCampaign);

        double campaignDiscount = shoppingCart.getCampaignDiscount();
        assertEquals(campaignDiscount, 82.5, 0);
    }

    @Test
    public void test_applyThreeDiscountsWithOneAmountDiscount() {
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
        shoppingCart.addItem(apple, 2); // 20
        shoppingCart.addItem(almond, 2); // 30
        shoppingCart.addItem(apple, 5); // 50

        shoppingCart.addItem(jean, 2); // 200
        shoppingCart.addItem(hat, 2); // 30
        shoppingCart.addItem(shirt, 2); // 100

        // total cost = 100 for food
        Campaign foodCampaign = new Campaign(food, 20.0, 4, DiscountType.Rate);
        // total cost = 100 for food
        Campaign foodAmountCampaign = new Campaign(food, 90.0, 4, DiscountType.Amount);

        // this campaign should  be applied since it is over the limit and better discount
        Campaign techCampaign = new Campaign(technology, 25.0, 6, DiscountType.Rate);

        // total discount should be 90 since it is the best discount
        shoppingCart.applyDiscounts(foodCampaign, foodAmountCampaign, techCampaign);

        double campaignDiscount = shoppingCart.getCampaignDiscount();
        assertEquals(campaignDiscount, 90.0, 0);
    }

    @Test
    public void test_applyCoupon() {
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
        shoppingCart.addItem(apple, 2); // 20
        shoppingCart.addItem(almond, 2); // 30
        shoppingCart.addItem(apple, 5); // 50

        shoppingCart.addItem(jean, 2); // 200
        shoppingCart.addItem(hat, 2); // 30
        shoppingCart.addItem(shirt, 2); // 100

        Coupon coupon = new Coupon(100, 10, DiscountType.Amount);

        // total discount should be 10 since it is the only discount
        shoppingCart.applyCoupon(coupon);

        double campaignDiscount = shoppingCart.getCouponDiscount();
        assertEquals(campaignDiscount, 10, 0);
    }

    @Test
    public void test_applyCouponBelowLimit() {
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
        shoppingCart.addItem(apple, 2); // 20
        shoppingCart.addItem(almond, 2); // 30
        shoppingCart.addItem(apple, 5); // 50

        shoppingCart.addItem(jean, 2); // 200
        shoppingCart.addItem(hat, 2); // 30
        shoppingCart.addItem(shirt, 2); // 100

        Coupon coupon = new Coupon(1000, 10, DiscountType.Amount);

        // total discount should be 10 since it is the only discount
        shoppingCart.applyCoupon(coupon);

        double campaignDiscount = shoppingCart.getCouponDiscount();
        assertEquals(campaignDiscount, 0, 0);
    }

    @Test
    public void test_applyBothCouponAndCampaigns() {
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
        shoppingCart.addItem(apple, 2); // 20
        shoppingCart.addItem(almond, 2); // 30
        shoppingCart.addItem(apple, 5); // 50

        shoppingCart.addItem(jean, 2); // 200
        shoppingCart.addItem(hat, 2); // 30
        shoppingCart.addItem(shirt, 2); // 100

        // total cost = 100 for food
        Campaign foodCampaign = new Campaign(food, 20.0, 4, DiscountType.Rate);
        // total cost = 100 for food
        Campaign foodAmountCampaign = new Campaign(food, 90.0, 4, DiscountType.Amount);

        // this campaign should  be applied since it is over the limit and better discount
        Campaign techCampaign = new Campaign(technology, 25.0, 6, DiscountType.Rate);

        Coupon coupon = new Coupon(100, 10, DiscountType.Amount);

        // total discount should be 90 since it is the best discount
        shoppingCart.applyDiscounts(foodCampaign, foodAmountCampaign, techCampaign);

        // apply coupon
        shoppingCart.applyCoupon(coupon);

        double campaignDiscount = shoppingCart.getCampaignDiscount();
        assertEquals(campaignDiscount, 90.0, 0);


        double couponDiscount = shoppingCart.getCouponDiscount();
        assertEquals(couponDiscount, 10, 0);

        assertEquals(shoppingCart.getTotalAmountAfterDiscounts(), 430 - 90 - 10, 0);
    }

}
