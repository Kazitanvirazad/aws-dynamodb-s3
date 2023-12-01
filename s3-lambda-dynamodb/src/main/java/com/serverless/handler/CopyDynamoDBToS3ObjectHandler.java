package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.serverless.model.ProfileCredential;
import com.serverless.service.ProductService;
import com.serverless.util.DynamoDBUtil;
import com.serverless.util.ResponseObject;
import com.serverless.util.SerializeUtil;
import com.serverless.util.ValidateUtil;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.utils.StringUtils;

import java.util.Map;

public class CopyDynamoDBToS3ObjectHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final String PROFILE_NAME = System.getenv("PROFILE_NAME");
    private static ProfileCredential profileCredential = DynamoDBUtil.getProfileCredentialByProfileName(PROFILE_NAME);

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        int statusCode;
        ResponseObject responseObject;
        String bucketName = System.getenv("S3BUCKET_NAME");
        Map<String, Object> requestBodyMap = ValidateUtil.parseRequestBody(input.getBody(), Map.class);

        String productId = requestBodyMap != null
                && requestBodyMap.containsKey("productid") ?
                (String) requestBodyMap.get("productid") : null;

        if (!StringUtils.isNotBlank(productId)) {
            statusCode = HttpStatusCode.BAD_REQUEST;
            responseObject = ResponseObject.builder()
                    .setMessage("Invalid Request Input!")
                    .setError(true)
                    .build();
        } else {
            if (ProductService.copyProductFromDynamoDBToS3(productId, profileCredential, bucketName)) {
                responseObject = ResponseObject.builder()
                        .setMessage("Product copied successfully from DynamoDB to S3 bucket !")
                        .build();
                statusCode = HttpStatusCode.CREATED;
            } else {
                statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;
                responseObject = ResponseObject.builder()
                        .setMessage("Failed to copy product!")
                        .setError(true)
                        .build();
            }
        }
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.withBody(SerializeUtil.serialize(responseObject)).setStatusCode(statusCode);
        return responseEvent;
    }
}
