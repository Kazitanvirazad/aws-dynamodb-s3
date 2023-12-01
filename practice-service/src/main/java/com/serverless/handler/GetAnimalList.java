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

public class GetAnimalList implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        List<Animal> animalList = AnimalService.getAnimalList();
        ResponseObject responseObject = null;
        int statusCode;
        if (animalList != null && animalList.size() > 0) {
            responseObject = ResponseObject.builder()
                    .setError(false)
                    .setData(animalList)
                    .build();
            statusCode = HttpStatusCode.OK;
        } else {
            responseObject = ResponseObject.builder()
                    .setError(true)
                    .setMessage("Animals not found!")
                    .build();
            statusCode = HttpStatusCode.NOT_FOUND;
        }
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.withBody(SerializeUtil.serialize(responseObject)).setStatusCode(statusCode);
        return responseEvent;
    }
}
