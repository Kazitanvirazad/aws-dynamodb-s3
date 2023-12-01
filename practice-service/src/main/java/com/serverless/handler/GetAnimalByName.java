package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.serverless.dto.Animal;
import com.serverless.service.AnimalService;
import com.serverless.util.ResponseObject;
import com.serverless.util.SerializeUtil;
import software.amazon.awssdk.http.HttpStatusCode;

import java.util.List;

public class GetAnimalByName implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        String name = apiGatewayProxyRequestEvent.getQueryStringParameters() != null
                && apiGatewayProxyRequestEvent.getQueryStringParameters().containsKey("name")
                ? apiGatewayProxyRequestEvent.getQueryStringParameters().get("name") : null;
        int statusCode;
        ResponseObject responseObject;
        if (name != null && name.length() > 0) {
            List<Animal> animal = AnimalService.getAnimalByName(name);
            if (animal != null && animal.size() > 0) {
                responseObject = ResponseObject.builder()
                        .setData(animal)
                        .build();
                statusCode = HttpStatusCode.OK;
            } else {
                responseObject = ResponseObject.builder()
                        .setError(true)
                        .setMessage("Animal not found!")
                        .build();
                statusCode = HttpStatusCode.NOT_FOUND;
            }
        } else {
            responseObject = ResponseObject.builder()
                    .setError(true)
                    .setMessage("Invalid query parameter!")
                    .build();
            statusCode = HttpStatusCode.BAD_REQUEST;
        }
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.withBody(SerializeUtil.serialize(responseObject)).setStatusCode(statusCode);
        return responseEvent;
    }
}
