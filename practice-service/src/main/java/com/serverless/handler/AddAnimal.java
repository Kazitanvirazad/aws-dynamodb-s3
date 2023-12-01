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

public class AddAnimal implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        boolean isAnimalAdded = false;
        ResponseObject responseObject = null;
        int statusCode;

        Animal animal = apiGatewayProxyRequestEvent.getBody() != null
                && apiGatewayProxyRequestEvent.getBody().length() > 0 ?
                SerializeUtil.deSerialize(apiGatewayProxyRequestEvent.getBody(), Animal.class)
                : null;
        if (animal != null) {
            isAnimalAdded = AnimalService.addAnimal(animal);
            responseObject = isAnimalAdded ? ResponseObject.builder()
                    .setError(false)
                    .setMessage("Animal added successfully!")
                    .build() :
                    ResponseObject.builder()
                            .setError(true)
                            .setMessage("Failed to add Animal. Something went wrong!")
                            .build();
            statusCode = !isAnimalAdded ? HttpStatusCode.INTERNAL_SERVER_ERROR :
                    HttpStatusCode.CREATED;
        } else {
            responseObject = ResponseObject.builder()
                    .setError(true)
                    .setMessage("Invalid request body input!")
                    .build();
            statusCode = HttpStatusCode.BAD_REQUEST;
        }
        APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent();
        apiGatewayProxyResponseEvent.withBody(SerializeUtil.serialize(responseObject)).setStatusCode(statusCode);
        return apiGatewayProxyResponseEvent;
    }
}
