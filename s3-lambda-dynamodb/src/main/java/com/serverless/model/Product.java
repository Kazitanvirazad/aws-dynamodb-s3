package com.serverless.model;

import com.serverless.dto.ProductDTO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.io.Serial;
import java.io.Serializable;


@DynamoDbBean
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = -1637591401152654654L;
    private String productId;
    private float price;
    private String color;
    private String productName;

    private String inventory;
    private ProductProperties properties;

    public Product() {
        super();
    }

    public Product(String productId, float price, String color, String productName, String inventory, ProductProperties properties) {
        this.productId = productId;
        this.price = price;
        this.color = color;
        this.productName = productName;
        this.inventory = inventory;
        this.properties = properties;
    }

    public Product(ProductDTO productDTO) {
        this.productId = productDTO.getProductId();
        this.price = productDTO.getPrice();
        this.color = productDTO.getColor();
        this.productName = productDTO.getProductName();
        this.inventory = productDTO.getInventory();
        this.properties = new ProductProperties(productDTO.getProperties());
    }

    @DynamoDbPartitionKey
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

    public ProductProperties getProperties() {
        return properties;
    }

    public void setProperties(ProductProperties properties) {
        this.properties = properties;
    }
}
