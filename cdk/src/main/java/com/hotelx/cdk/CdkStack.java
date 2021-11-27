package com.hotelx.cdk;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;

import java.util.Map;

public class CdkStack extends Stack {

    private final int MEMORY_SIZE = 512;

    public CdkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public CdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The code that defines your stack goes here

    }

    Function createLambda(String functionName, String handler, String jarFileLocation, Map<String, String> envVariables) {
        var function = Function.Builder.create(this, functionName)
                .code(Code.fromAsset(jarFileLocation))
                .functionName(functionName)
                .memorySize(MEMORY_SIZE)
                .handler(handler)
                .runtime(Runtime.JAVA_11)
                .environment(envVariables)
                .build();

        return function;
    }

    void createDdbTable() {}

    void createRestApiForLambda(String name, Function handler) {
        LambdaRestApi.Builder
                .create(this, name)
                .handler(handler)
                .build();
    }

    void grantFunctionReadWriteToDdbTable(Function function, Table table) {
        table.grantReadWriteData(function);
    }
}
