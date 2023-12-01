package com.serverless.service;

import com.serverless.dto.ProductDTO;
import com.serverless.model.Product;
import com.serverless.model.ProfileCredential;
import com.serverless.util.DynamoDBUtil;
import com.serverless.util.SerializeUtil;
import com.serverless.util.ValidateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class ProductService {
    private static Logger LOGGER = LogManager.getLogger(ProductService.class);
    private static final String REGION = System.getenv("REGION");

    public static boolean addProductToDynamoDB(ProductDTO productDTO) {
        boolean isProductAdded = false;
        Product product = new Product(productDTO);
        DynamoDbTable<Product> productTable = DynamoDBUtil.PRODUCTINVENTORY_TABLE;

        try {
            productTable.putItem(product);
            isProductAdded = true;
        } catch (UnsupportedOperationException e) {
            LOGGER.error(e.getMessage());
        }
        return isProductAdded;
    }

    private static boolean addProductToS3(String productId, String productJson, ProfileCredential profileCredential, String bucketName) {
        boolean isProductAdded = false;
        try (S3Client s3Client = S3Client.builder()
                .region(Region.of(REGION))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials
                        .create(profileCredential.getAccessKey(), profileCredential.getSecretKey())))
                .build()) {
            s3Client.putObject(builder ->
                            builder.bucket(bucketName)
                                    .key(productId + ".json")
                                    .contentType("application/json").build(),
                    RequestBody.fromString(productJson));
            isProductAdded = true;
        } catch (S3Exception e) {
            LOGGER.error(e.getMessage());
        }
        return isProductAdded;
    }

    public static boolean addProductToS3(ProductDTO productDTO, ProfileCredential profileCredential, String bucketName) {
        Product product = new Product(productDTO);
        String productJson = SerializeUtil.serialize(product);
        return addProductToS3(product.getProductId(), productJson, profileCredential, bucketName);
    }

    public static boolean copyProductFromS3ToDynamoDB(String productId, ProfileCredential profileCredential, String bucketName) {
        boolean isProductCopied = false;

        DynamoDbTable<Product> productTable = DynamoDBUtil.PRODUCTINVENTORY_TABLE;

        try (S3Client s3Client = S3Client.builder()
                .region(Region.of(REGION))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials
                        .create(profileCredential.getAccessKey(), profileCredential.getSecretKey())))
                .build();
             ResponseInputStream<GetObjectResponse> productResponseInputStream
                     = s3Client.getObject(builder -> builder.bucket(bucketName).key(productId + ".json").build());
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(productResponseInputStream))) {

            StringBuffer productJson = new StringBuffer();
            bufferedReader.lines().forEach(line -> productJson.append(line));
            if (!productJson.isEmpty()) {
                Product product = ValidateUtil.parseRequestBody(productJson.toString(), Product.class);
                if (ValidateUtil.isValidProduct(product)) {
                    try {
                        productTable.putItem(product);
                        isProductCopied = true;
                    } catch (UnsupportedOperationException e) {
                        LOGGER.error(e.getMessage());
                    }
                }
            }
        } catch (S3Exception e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        return isProductCopied;
    }

    public static boolean copyProductFromDynamoDBToS3(String productId, ProfileCredential profileCredential, String bucketName) {
        boolean isProductCopied = false;

        DynamoDbTable<Product> productTable = DynamoDBUtil.PRODUCTINVENTORY_TABLE;
        try {
            Product product = productTable.getItem(Key.builder().partitionValue(productId).build());
            if (ValidateUtil.isValidProduct(product)) {
                String productJson = SerializeUtil.serialize(product);
                if (StringUtils.isNotBlank(productJson) && addProductToS3(product.getProductId(), productJson, profileCredential, bucketName)) {
                    isProductCopied = true;
                }
            }
        } catch (UnsupportedOperationException e) {
            LOGGER.error(e.getMessage());
        }

        return isProductCopied;
    }
}
