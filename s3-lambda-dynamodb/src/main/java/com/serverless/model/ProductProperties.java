package com.serverless.model;

import com.serverless.dto.ProductPropertiesDTO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class ProductProperties {
    private String category;
    private String connectionType;

    public ProductProperties() {
        super();
    }

    public ProductProperties(String category, String connectionType) {
        this.category = category;
        this.connectionType = connectionType;
    }

    public ProductProperties(ProductPropertiesDTO productPropertiesDTO) {
        this.category = productPropertiesDTO.getCategory();
        this.connectionType = productPropertiesDTO.getConnectionType();
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
