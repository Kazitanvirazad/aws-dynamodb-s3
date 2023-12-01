package com.serverless.util;

import com.serverless.dto.ProductDTO;
import com.serverless.model.Product;
import software.amazon.awssdk.utils.StringUtils;

public class ValidateUtil {

    public static <T> T parseRequestBody(String requestBody, Class clazz) {
        if (StringUtils.isNotBlank(requestBody)) {
            return SerializeUtil.deSerialize(requestBody, clazz);
        }
        return null;
    }

    public static boolean isValidProductDTO(ProductDTO productDTO) {
        return (productDTO != null && productDTO.getProductId() != null
                && StringUtils.isNotBlank(productDTO.getProductId())
                && productDTO.getProductName() != null
                && StringUtils.isNotBlank(productDTO.getProductName()));
    }

    public static boolean isValidProduct(Product product) {
        return (product != null && product.getProductId() != null
                && StringUtils.isNotBlank(product.getProductId())
                && product.getProductName() != null
                && StringUtils.isNotBlank(product.getProductName()));
    }
}
