package com.serverless.dto;

import com.serverless.model.ProductProperties;

import java.io.Serializable;

public final class ProductPropertiesDTO implements Serializable {
    private String category;
    private String connectionType;

    public ProductPropertiesDTO() {
        super();
    }

    public ProductPropertiesDTO(String category, String connectionType) {
        this.category = category;
        this.connectionType = connectionType;
    }

    public ProductPropertiesDTO(ProductProperties properties) {
        this.category = properties.getCategory();
        this.connectionType = properties.getConnectionType();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }
}
