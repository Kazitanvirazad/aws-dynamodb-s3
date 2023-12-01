package com.serverless.dto;

import com.serverless.model.Product;

import java.io.Serializable;

public final class ProductDTO implements Serializable {

    private String productId;
    private float price;
    private String color;
    private String productName;

    private String inventory;
    private ProductPropertiesDTO properties;

    public ProductDTO() {
        super();
    }

    public ProductDTO(String productId, float price, String color, String productName, String inventory, ProductPropertiesDTO properties) {
        this.productId = productId;
        this.price = price;
        this.color = color;
        this.productName = productName;
        this.inventory = inventory;
        this.properties = properties;
    }

    public ProductDTO(Product product) {
        this.productId = product.getProductId();
        this.price = product.getPrice();
        this.color = product.getColor();
        this.productName = product.getProductName();
        this.inventory = product.getInventory();
        this.properties = new ProductPropertiesDTO(product.getProperties());
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public ProductPropertiesDTO getProperties() {
        return properties;
    }

    public void setProperties(ProductPropertiesDTO properties) {
        this.properties = properties;
    }
}
