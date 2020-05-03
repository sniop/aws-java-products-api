# Serverless REST API in Java/Maven using DynamoDB


![image](https://user-images.githubusercontent.com/8188/38645675-ec708d0e-3db2-11e8-8f8b-a4a37ed612b9.png)


The sample serverless service will create a REST API for products. It will be deployed to AWS. The data will be stored in a DynamoDB table.

This is a companion app for the blog post [REST API in Java using DynamoDB and Serverless](https://serverless.com/blog/how-to-create-a-rest-api-in-java-using-dynamodb-and-serverless/).

## Install Pre-requisites

* `node` and `npm`
* Install the JDK and NOT the Java JRE from [Oracle JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html). And set the following:
`export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-10.jdk/Contents/Home`
* [Apache Maven](https://maven.apache.org/). After [downloading](https://maven.apache.org/download.html) and [installing](https://maven.apache.org/install.html) Apache Maven, please add the `apache-maven-x.x.x` folder to the `PATH` environment variable.

### Test Pre-requisites

Test Java installation:

```
$ java --version

java 10 2018-03-20
Java(TM) SE Runtime Environment 18.3 (build 10+46)
Java HotSpot(TM) 64-Bit Server VM 18.3 (build 10+46, mixed mode)
```

Test Maven installation:

```
$ mvn -v

Apache Maven 3.5.3 (3383c37e1f9e9b3bc3df5050c29c8aff9f295297; 2018-02-24T14:49:05-05:00)
Maven home: /usr/local/apache-maven-3.5.3
Java version: 10, vendor: Oracle Corporation
Java home: /Library/Java/JavaVirtualMachines/jdk-10.jdk/Contents/Home
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "10.13.3", arch: "x86_64", family: "mac"
```

## Build the Java project

Create the java artifact (jar) by:

```
$ cd aws-java-products-api
$ mvn clean install
```

We can see that we have an artifact in the `target` folder named `products-api-dev.jar`.

## Running the code locally using serverless-offline

* java-invoke-local setup for debugging
    * Setup Intellij-Jar Application
        * name : java-invoke-local-server
        * path-to-jar: ~/aws-java-products-api/node_modules/java-invoke-local/build/libs/java-invoke-local-all.jar
        * program-args: --server
            * this will start the server
        * Environment Variables
            * PRODUCTS_TABLE_NAME=java-products-dev;IS_OFFLINE=true
                * not sure why but java-invoke-local-server does not seems to have access to environment variables defined in serverless.yml file , 
                they these not to be defined again
    * Start Java-invoke-local-server in debug mode
```
$ sls offline start  
```


## Deploy the serverless app

```
$ sls deploy
```

## Test the API

Let's invoke each of the four functions that we created as part of the app.

### Create Product

```
$ curl -X POST https://xxxxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/products -d '{"name": "Product1", "price": 9.99}'

{"id":"ba04f16b-f346-4b54-9884-957c3dff8c0d","name":"Product1","price":9.99}
```

### List Products

```
$ curl https://xxxxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/products

[{"id":"dfe41235-0fe5-4e6f-9a9a-19b7b7ee79eb","name":"Product3","price":7.49},
{"id":"ba04f16b-f346-4b54-9884-957c3dff8c0d","name":"Product1","price":9.99},
{"id":"6db3efe0-f45c-4c5f-a73c-541a4857ae1d","name":"Product4","price":2.69},
{"id":"370015f8-a8b9-4498-bfe8-f005dbbb501f","name":"Product2","price":5.99},
{"id":"cb097196-d659-4ba5-b6b3-ead4c07a8428","name":"Product5","price":15.49}]
```

**No Product(s) Found:**

```
$ curl https://xxxxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/products

[]
```

### Get Product

```
$ curl https://xxxxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/products/ba04f16b-f346-4b54-9884-957c3dff8c0d

{"id":"ba04f16b-f346-4b54-9884-957c3dff8c0d","name":"Product1","price":9.99}
```

**Product Not Found:**

```
curl https://xxxxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/products/xxxx

"Product with id: 'xxxx' not found."
```

### DeleteProduct

```
$ curl -X DELETE https://xxxxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/products/24ada348-07e8-4414-8a8f-7903a6cb0253
```

**Product Not Found:**

```
curl -X DELETE https://xxxxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/products/xxxx

"Product with id: 'xxxx' not found."
```

## View the CloudWatch Logs

```
$ serverless logs --function getProduct
```

## View the Metrics

View the metrics for the service:

```
$ serverless metrics

Service wide metrics
April 2, 2018 2:11 PM - April 3, 2018 2:11 PM

Invocations: 2
Throttles: 0
Errors: 0
Duration (avg.): 331.23ms
```

Or, view the metrics for only one function:

```
$ serverless metrics --function hello

hello
April 2, 2018 2:13 PM - April 3, 2018 2:13 PM

Invocations: 2
Throttles: 0
Errors: 0
Duration (avg.): 331.23ms
```
