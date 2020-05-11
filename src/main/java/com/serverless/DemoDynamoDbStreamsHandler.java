package com.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import org.apache.log4j.Logger;

import java.util.Collections;

public class DemoDynamoDbStreamsHandler implements RequestHandler<DynamodbEvent, ApiGatewayResponse> {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Override
    public ApiGatewayResponse handleRequest(DynamodbEvent dbEvent, Context context) {

        logger.info("START-Process-Stream-Records");
        try {
            for (DynamodbStreamRecord record : dbEvent.getRecords()) {
                logger.info(record);
            }
            logger.info("END-Process-Stream-Records");
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        } catch (Exception ex) {
            logger.error("Error in retrieving product: " + ex);

            // send the error response back
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setRawBody("error=" + ex.getMessage())
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        }
    }
}
