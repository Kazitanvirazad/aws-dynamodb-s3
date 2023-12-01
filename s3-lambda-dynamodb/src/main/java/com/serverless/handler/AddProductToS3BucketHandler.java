package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.serverless.dto.ProductDTO;
import com.serverless.model.ProfileCredential;
import com.serverless.service.ProductService;
import com.serverless.util.DynamoDBUtil;
import com.serverless.util.ResponseObject;
import com.serverless.util.SerializeUtil;
import com.serverless.util.ValidateUtil;
import software.amazon.awssdk.http.HttpStatusCode;

public class AddProductToS3BucketHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final String PROFILE_NAME = System.getenv("PROFILE_NAME");
    private static ProfileCredential profileCredential = DynamoDBUtil.getProfileCredentialByProfileName(PROFILE_NAME);

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        int statusCode;
        ResponseObject responseObject;
        String bucketName = System.getenv("S3BUCKET_NAME");
        ProductDTO productDTO = ValidateUtil.parseRequestBody(input.getBody(), ProductDTO.class);
        if (!ValidateUtil.isValidProductDTO(productDTO)) {
            statusCode = HttpStatusCode.BAD_REQUEST;
            responseObject = ResponseObject.builder()
                    .setMessage("Invalid Request Input!")
                    .setError(true)
                    .build();
        } else {
            boolean isProductAdded = ProductService.addProductToS3(productDTO, profileCredential, bucketName);
            if (isProductAdded) {
                responseObject = ResponseObject.builder()
                        .setMessage("Product added successfully to S3 bucket: " + bucketName + "!")
                        .build();
                statusCode = HttpStatusCode.CREATED;
            } else {
                statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;
                responseObject = ResponseObject.builder()
                        .setMessage("Failed to add product!")
                        .setError(true)
                        .build();
            }
        }
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.withBody(SerializeUtil.serialize(responseObject)).setStatusCode(statusCode);
        return responseEvent;
    }
}
