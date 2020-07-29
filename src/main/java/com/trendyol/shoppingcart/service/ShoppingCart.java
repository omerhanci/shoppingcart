package com.trendyol.shoppingcart.service;

import com.trendyol.shoppingcart.enums.DiscountType;
import com.trendyol.shoppingcart.model.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ShoppingCart {

    private Map<Category, ProductQuantityMap> categoryProductQuantityMap = new HashMap<Category, ProductQuantityMap>();
    private double totalAmountAfterDiscounts = 0;
    private double couponDiscount = 0;
    private double campaignDiscount = 0;
    private double deliveryCost = 0;


    /**
     * Adds the given product to the cart with given quantity
     * @param product
     * @param quantity
     */
    public void addItem(Product product, int quantity) {
        // if category does not already exists, we should add it to the map
        if (!categoryProductQuantityMap.containsKey(product.getCategory())) {
            ProductQuantityMap productQuantityMap = createProductQuantityMap(product, quantity);
            categoryProductQuantityMap.put(product.getCategory(), productQuantityMap);
        }
        // if category exists but product does not exists, we should add the product to the map
        else if (!categoryProductQuantityMap.get(product.getCategory()).getProductQuantityMap().containsKey(product)) {
            ProductQuantityMap productQuantityMap = categoryProductQuantityMap.get(product.getCategory());
            categoryProductQuantityMap.replace(product.getCategory(), addNewProductToMap(productQuantityMap, product, quantity));
        }
        // if both category and product exists we should just update the quantity
        else {
            ProductQuantityMap productQuantityMap = categoryProductQuantityMap.get(product.getCategory());
            updateProductQuantity(productQuantityMap, product, quantity);
        }

        // update total amount
        totalAmountAfterDiscounts += product.getPrice() * quantity;
    }

    /**
     * Apply the best discount among the given campaigns can provide
     * @param campaigns
     */
    public void applyDiscounts(Campaign... campaigns) {
        double maxDiscount = 0;
        for (Campaign campaign : campaigns) {
            double discount = calculateDiscount(campaign);
            if (discount > maxDiscount) {
                maxDiscount = discount;
            }
        }
        // only apply the best discount
        campaignDiscount = maxDiscount;
        totalAmountAfterDiscounts -= campaignDiscount;
    }

    /**
     * Applies the given coupon to the cart
     * @param coupon
     */
    public void applyCoupon(Coupon coupon) {
        double totalPrice = 0;
        // get total price
        for (Map.Entry<Category, ProductQuantityMap> category : categoryProductQuantityMap.entrySet()) {
            for (Map.Entry<Product, Integer> productQuantityEntry : categoryProductQuantityMap.get(category.getKey()).getProductQuantityMap().entrySet()) {
                totalPrice += productQuantityEntry.getKey().getPrice() * productQuantityEntry.getValue(); // total price equals price times quantity
            }
        }
        // do we exceed the limit ?
        if (totalPrice >= coupon.getMinAmount()) {
            if (coupon.getDiscountType().equals(DiscountType.Rate)) {
                couponDiscount = totalPrice * coupon.getDiscountAmount() / 100;
            } else if (coupon.getDiscountType().equals(DiscountType.Amount)) {
                couponDiscount = coupon.getDiscountAmount();
            }
        }

        // update the total discount
        totalAmountAfterDiscounts -= couponDiscount;
    }

    /**
     * Prints the current invoice with applied discounts, coupons, delivery costs and total amount
     */
    public void print() {
        String invoice = "";
        for (Map.Entry<Category, ProductQuantityMap> categoryProductQuantityMapEntry : categoryProductQuantityMap.entrySet()) {
            invoice += categoryProductQuantityMapEntry.getKey().getTitle() + "\n";
            for (Map.Entry<Product, Integer> productQuantityEntry : categoryProductQuantityMap.get(categoryProductQuantityMapEntry.getKey()).getProductQuantityMap().entrySet()) {
                invoice += "\t" + productQuantityEntry.getKey().getTitle() + "\t\t" + productQuantityEntry.getKey().getPrice() + "\t" + productQuantityEntry.getValue() + "\t" + productQuantityEntry.getKey().getPrice() * productQuantityEntry.getValue() + "\n";
            }
        }
        System.out.println("\n");
        invoice += "Discount: \t\t-" + campaignDiscount + "\n";
        invoice += "Coupon:   \t\t-" + couponDiscount + "\n";
        invoice += "Total:    \t\t " + totalAmountAfterDiscounts + "\n";
        invoice += "Delivery Cost: \t" + deliveryCost;
        System.out.println(invoice);
    }

    /**
     * Returns the number of deliveries
     * @return
     */
    public int getNumberOfDeliveries() {
        return categoryProductQuantityMap.size();
    }


    /**
     * Returns the number of products
     * @return
     */
    public int getNumberOfProducts() {
        int numberOfProducts = 0;
        for (Map.Entry<Category, ProductQuantityMap> productQuantityEntry : categoryProductQuantityMap.entrySet()) {
            numberOfProducts += productQuantityEntry.getValue().getProductQuantityMap().size();
        }
        return numberOfProducts;
    }

    /**
     * Calculates dicount amount for given campaign
     * @param campaign
     * @return
     */
    private double calculateDiscount(Campaign campaign) {
        // if we do not have any campaign for this category
        if (!categoryProductQuantityMap.containsKey(campaign.getCategory())) {
            return 0;
        }
        int numberOfItems = 0;
        double totalPrice = 0;
        for (Map.Entry<Product, Integer> productQuantityEntry : categoryProductQuantityMap.get(campaign.getCategory()).getProductQuantityMap().entrySet()) {
            numberOfItems += productQuantityEntry.getValue();
            totalPrice += productQuantityEntry.getKey().getPrice() * productQuantityEntry.getValue(); // total price equals price times quantity
        }

        // if we did not pass the limit, then no discount
        if (numberOfItems < campaign.getLimit()) {
            return 0;
        }

        // discount applies
        if (campaign.getDiscountType().equals(DiscountType.Rate)) {
            return totalPrice * campaign.getRate() / 100;
        } else if (campaign.getDiscountType().equals(DiscountType.Amount)) {
            return campaign.getRate();
        } else {
            return 0; // not possible at the moment
        }
    }

    /**
     * Creates a new product quantity map
     * @param product
     * @param quantity
     * @return
     */
    private ProductQuantityMap createProductQuantityMap(Product product, int quantity) {
        Map<Product, Integer> productQuantityMap = new HashMap<Product, Integer>();
        productQuantityMap.put(product, quantity);
        return new ProductQuantityMap(productQuantityMap);
    }

    /**
     * adds new product to the product quantity map
     * @param productQuantityMap
     * @param product
     * @param quantity
     * @return
     */
    private ProductQuantityMap addNewProductToMap(ProductQuantityMap productQuantityMap, Product product, int quantity) {
        productQuantityMap.getProductQuantityMap().put(product, quantity);
        return productQuantityMap;
    }

    /**
     * updates existed products quantity
     * @param productQuantityMap
     * @param product
     * @param quantity
     */
    private void updateProductQuantity(ProductQuantityMap productQuantityMap, Product product, int quantity) {
        int oldQuantity = productQuantityMap.getProductQuantityMap().get(product);
        productQuantityMap.getProductQuantityMap().replace(product, oldQuantity + quantity);
    }
}
