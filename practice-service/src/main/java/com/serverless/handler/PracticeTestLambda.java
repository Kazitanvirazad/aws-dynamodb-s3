package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.serverless.dto.Game;
import com.serverless.util.ResponseObject;
import com.serverless.util.SerializeUtil;
import software.amazon.awssdk.http.HttpStatusCode;

public class PracticeTestLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent();
        ResponseObject responseObject = ResponseObject.builder()
                .setError(false)
                .setMessage("Practice Test Lambda execute successfully!")
                .setData(SerializeUtil.deSerialize(apiGatewayProxyRequestEvent.getBody(), Game.class))
                .build();
        apiGatewayProxyResponseEvent
                .withBody(SerializeUtil.serialize(responseObject))
                .setStatusCode(HttpStatusCode.OK);
        return apiGatewayProxyResponseEvent;
    }
}
