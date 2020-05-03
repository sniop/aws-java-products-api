package com.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.dal.Product;
import org.apache.log4j.Logger;

import java.util.Collections;

public class UpdateProductHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        try {
            // get the 'body' from input
            JsonNode body = new ObjectMapper().readTree(input.getBody());

            // create the Product object for post
            Product product = new Product();
            product.setId(body.get("id").asText());
            product.setName(body.get("name").asText());
            product.setPrice((float) body.get("price").asDouble());
            product.update(product);

            // send the response back
            return new APIGatewayProxyResponseEvent()
                    .withBody(convertToText(product))
                    .withStatusCode(200)
                    .withHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"));

        } catch (Exception ex) {
            logger.error("Error in saving product: " + ex);

            // send the error response back
            return new APIGatewayProxyResponseEvent()
                    .withBody("Error in saving product")
                    .withStatusCode(500)
                    .withHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"));
        }
    }

    public String convertToText(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
