package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;
import com.serverless.model.ProfileCredential;
import com.serverless.model.S3Output;
import com.serverless.util.DynamoDBUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FirstS3LambdaHandler implements RequestHandler<S3Event, Void> {
    private static Logger LOGGER = LogManager.getLogger(FirstS3LambdaHandler.class);
    private static final String PROFILE_NAME = System.getenv("PROFILE_NAME");
    private static final String REGION = System.getenv("REGION");

    private static ProfileCredential profileCredential = DynamoDBUtil.getProfileCredentialByProfileName(PROFILE_NAME);

    @Override
    public Void handleRequest(S3Event s3Event, Context context) {
        List<String> output = new ArrayList<>();
        if (s3Event == null || s3Event.getRecords() == null || s3Event.getRecords().isEmpty()) {
            LOGGER.error("No records found!", LogLevel.ERROR);
        }
        for (S3EventNotification.S3EventNotificationRecord record : s3Event.getRecords()) {
            String bucketName = record.getS3().getBucket().getName();
            String objectKey = record.getS3().getObject().getKey();
            String itemUpdateTimeStamp = record.getEventTime().toInstant().toString();

            try (S3Client s3Client = S3Client.builder()
                    .region(Region.of(REGION))
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials
                            .create(profileCredential.getAccessKey(), profileCredential.getSecretKey())))
                    .build();
                 ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject
                         (builder -> builder.bucket(bucketName).key(objectKey).build());
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                bufferedReader.lines().forEach(line -> {
                    if (output.size() == 0) {
                        output.add("****This file is an output of " + objectKey + " processed in a Lambda Function.****\n\n");
                    }
                    output.add(line + "\n");
                });
                String itemCreationTimeStamp = DynamoDBUtil.getItemCreationTimeStamp(objectKey);
                DynamoDBUtil.putS3outputItem(new S3Output(objectKey, output, (itemCreationTimeStamp != null ? itemCreationTimeStamp : itemUpdateTimeStamp), itemUpdateTimeStamp));
//                s3Client.putObject(PutObjectRequest.builder()
//                        .bucket(bucketName)
//                        .key(objectKey.substring(0, objectKey.length() - 4) + "-copy.txt")
//                        .contentType("text/plain")
//                        .build(), RequestBody.fromString(output.toString()));
            } catch (IOException e) {
                LOGGER.error("Error occurred in creating BufferedReader");
            }

        }
        return null;
    }
}
