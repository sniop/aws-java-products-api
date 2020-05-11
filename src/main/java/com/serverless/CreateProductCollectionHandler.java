package com.serverless;

import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.dal.DynamoDBAdapter;
import com.serverless.dal.Product;
import com.serverless.dal.ProductCollection;
import org.apache.log4j.Logger;

import java.util.*;

import static com.serverless.dal.Product.*;

public class CreateProductCollectionHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Override
    public ApiGatewayResponse handleRequest(APIGatewayProxyRequestEvent request, Context context) {

        try {
            // get the 'body' from input
            ProductCollection productCollection = new ObjectMapper().readValue(request.getBody(), ProductCollection.class);


            logger.debug("productCollection=" + productCollection);

            QueryRequest queryRequest = new QueryRequest()
                    .withTableName(PRODUCTS_TABLE_NAME)
                    .withKeyConditionExpression(KEY_PARTITION + "=:v_Id")
                    .addExpressionAttributeValuesEntry(":v_Id", new AttributeValue(productCollection.getProducts().get(0).getId()))
                    .withLimit(1);

            QueryResult queryResult = DynamoDBAdapter.getInstance().getDbClient().query(queryRequest);
            if (queryResult.getCount() <= 0) {
                Response responseBody = new Response("Product-Id should already exist");
                return ApiGatewayResponse.builder()
                        .setStatusCode(500)
                        .setObjectBody(responseBody)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            }

            Collection<TransactWriteItem> actions = new ArrayList<>();

            for (Product product : productCollection.getProducts()) {

                final Map<String, AttributeValue> productItem = new HashMap<>();
                productItem.put(KEY_PARTITION, new AttributeValue(product.getId()));
                productItem.put(KEY_SORT, new AttributeValue(product.getName()));
                productItem.put(ATTRIBUTE_PRICE, new AttributeValue().withN(product.getPrice().toString()));

                Put createProduct = new Put()
                        .withTableName(PRODUCTS_TABLE_NAME)
                        .withItem(productItem)
                        .withReturnValuesOnConditionCheckFailure(ReturnValuesOnConditionCheckFailure.ALL_OLD);

                actions.add(new TransactWriteItem().withPut(createProduct));
            }

/*
            for (Product product : productCollection.getProducts()) {

                final Map<String, AttributeValue> productItem = new HashMap<>();
                productItem.put(KEY_PARTITION, new AttributeValue(product.getId()));
                productItem.put(KEY_SORT, new AttributeValue(product.getName()));
//                productItem.put(ATTRIBUTE_PRICE, new AttributeValue().withN(product.getPrice().toString()));

                Update updateProduct = new Update()
                        .withTableName(PRODUCTS_TABLE_NAME)
                        .withKey(productItem)
                        .withUpdateExpression("SET price = :v_price")
                        .addExpressionAttributeValuesEntry(":v_price",new AttributeValue().withN(product.getPrice().toString()))
//					    .withConditionExpression("attribute_exists(" + KEY_PARTITION + ")")
                        .withReturnValuesOnConditionCheckFailure(ReturnValuesOnConditionCheckFailure.ALL_OLD);

                actions.add(new TransactWriteItem().withUpdate(updateProduct));
            }
*/


            TransactWriteItemsRequest placeOrderTransaction = new TransactWriteItemsRequest()
                    .withTransactItems(actions)
                    .withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);

            DynamoDBAdapter.getInstance().getDbClient().transactWriteItems(placeOrderTransaction);
            System.out.println("Transaction Successful");


            // send the response back
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception ex) {
            logger.error("Error in saving product: " + ex);

            // send the error response back
            Response responseBody = new Response("Error in saving product: ");
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        }
    }
}
