package com.serverless.dal;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

public class DynamoDBAdapter {

    private static DynamoDBAdapter db_adapter = null;
    private final AmazonDynamoDB client;
    private DynamoDBMapper mapper;

    private DynamoDBAdapter() {
        // create the client
        String isOfflineText = System.getenv("IS_OFFLINE");
        if (Boolean.valueOf(isOfflineText)) {

            this.client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "localhost"))
                    .build();
        } else {
            this.client = AmazonDynamoDBClientBuilder.standard()
                    .withRegion(Regions.US_EAST_2)
                    .build();
        }
    }

    public static DynamoDBAdapter getInstance() {
        if (db_adapter == null)
            db_adapter = new DynamoDBAdapter();

        return db_adapter;
    }

    public AmazonDynamoDB getDbClient() {
        return this.client;
    }

    public DynamoDBMapper createDbMapper(DynamoDBMapperConfig mapperConfig) {
        // create the mapper with the mapper config
        if (this.client != null)
            mapper = new DynamoDBMapper(this.client, mapperConfig);

        return this.mapper;
    }


}
