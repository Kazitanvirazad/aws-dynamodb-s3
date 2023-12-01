package com.serverless.util;

import com.serverless.handler.FirstS3LambdaHandler;
import com.serverless.model.Product;
import com.serverless.model.ProfileCredential;
import com.serverless.model.S3Output;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public final class DynamoDBUtil {
    private static Logger LOGGER = LogManager.getLogger(FirstS3LambdaHandler.class);

    private static String REGION = System.getenv("REGION");
    private static String DYNAMODB_CREDENTIALTABLE_NAME = System.getenv("DYNAMODB_CREDENTIALTABLE_NAME");
    private static String DYNAMODB_S3OUTPUTTABLE_NAME = System.getenv("DYNAMODB_S3OUTPUTTABLE_NAME");
    private static String DYNAMODB_PRODUCTINVENTORYTABLE_NAME = System.getenv("DYNAMODB_PRODUCTINVENTORYTABLE_NAME");
    private static Region awsRegion = Region.of(REGION);

    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(awsRegion)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    private static DynamoDbTable<ProfileCredential> PROFILECREDENTIAL_TABLE = enhancedClient.table(DYNAMODB_CREDENTIALTABLE_NAME,
            TableSchema.fromBean(ProfileCredential.class));
    private static DynamoDbTable<S3Output> S3OUTPUT_TABLE = enhancedClient.table(DYNAMODB_S3OUTPUTTABLE_NAME,
            TableSchema.fromBean(S3Output.class));
    public static DynamoDbTable<Product> PRODUCTINVENTORY_TABLE = enhancedClient.table(DYNAMODB_PRODUCTINVENTORYTABLE_NAME,
            TableSchema.fromBean(Product.class));

    public static ProfileCredential getProfileCredentialByProfileName(String profile_name) {
        ProfileCredential profileCredential = PROFILECREDENTIAL_TABLE.getItem(Key.builder()
                .partitionValue(profile_name)
                .build());

        if (profileCredential.isValid()) {
            return profileCredential;
        }
        return null;
    }

    public static void putS3outputItem(S3Output s3Output) {
        S3OUTPUT_TABLE.putItem(s3Output);
    }

    public static String getItemCreationTimeStamp(String objectKey) {
        S3Output s3Output;
        String timeStamp = null;
        try {
            s3Output = S3OUTPUT_TABLE.getItem(Key.builder()
                    .partitionValue(objectKey)
                    .build());
            if (s3Output != null && s3Output.getFileName() != null && s3Output.getItemCreationTimeStamp() != null) {
                timeStamp = s3Output.getItemCreationTimeStamp();
            }
        } catch (UnsupportedOperationException e) {
            LOGGER.error(e.getMessage());
        }
        return timeStamp;
    }
}
