package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.serverless.service.AnimalService;
import com.serverless.util.ResponseObject;
import com.serverless.util.SerializeUtil;
import software.amazon.awssdk.http.HttpStatusCode;

import java.util.Map;

public class UpdateAnimalByFood implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        Map<String, Object> requestInput = apiGatewayProxyRequestEvent.getBody() != null
                && apiGatewayProxyRequestEvent.getBody().length() > 0 ?
                SerializeUtil.deSerialize(apiGatewayProxyRequestEvent.getBody(), Map.class) :
                null;
        int statusCode;
        ResponseObject responseObject;

        if (requestInput != null && requestInput.containsKey("foodType") && requestInput.containsKey("key") && requestInput.size() > 1) {
            boolean isAnimalUpdated = AnimalService.updateAnimalByFood(requestInput);
            if (isAnimalUpdated) {
                responseObject = ResponseObject.builder()
                        .setMessage("Update Successful!")
                        .build();
                statusCode = HttpStatusCode.ACCEPTED;
            } else {
                responseObject = ResponseObject.builder()
                        .setError(true)
                        .setMessage("Animal update failed!")
                        .build();
                statusCode = HttpStatusCode.NOT_FOUND;
            }
        } else {
            responseObject = ResponseObject.builder()
                    .setError(true)
                    .setMessage("Invalid request!")
                    .build();
            statusCode = HttpStatusCode.BAD_REQUEST;
        }

        APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent();
        apiGatewayProxyResponseEvent.withBody(SerializeUtil.serialize(responseObject)).setStatusCode(statusCode);
        return apiGatewayProxyResponseEvent;
    }
}
