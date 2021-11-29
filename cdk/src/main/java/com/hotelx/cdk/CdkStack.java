package com.hotelx.cdk;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.stepfunctions.*;
import software.amazon.awscdk.services.stepfunctions.tasks.LambdaInvoke;
import software.amazon.awscdk.services.stepfunctions.tasks.LambdaInvokeProps;

import java.util.Map;

public class CdkStack extends Stack {

    private final int MEMORY_SIZE = 256;
    private final Duration FUNCTION_TIMEOUT = Duration.minutes(3);

    public CdkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public CdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        var roomsTable = createDdbTable("Rooms");
        var bookingFunction = createLambda("booking-service",
                "com.hotelx.booking.boundary.FunctionHandler::handle",
                "../booking-service/target/function.jar",
                Map.of("DDB_TABLE_NAME", roomsTable.getTableName()));

        grantFunctionReadWriteToDdbTable(bookingFunction, roomsTable);

        var paymentsTable = createDdbTable("Payments");
        var paymentsFunction = createLambda("payments-service",
                "com.hotelx.payments.boundary.FunctionHandler::handle",
                "../payments-service/target/function.jar",
                Map.of("DDB_TABLE_NAME", paymentsTable.getTableName()));

        grantFunctionReadWriteToDdbTable(paymentsFunction, paymentsTable);

        var bookingRoomSuccess = new Succeed(this, "Booked Room successfully");
        var bookingRoomFailed = new Fail(this, "Booking Room failed", FailProps.builder()
                .error("Job failed!!")
                .build());

        var cancelRoomReservationTask = new LambdaInvoke(this, "CancelRoomReservation", LambdaInvokeProps.builder()
                .lambdaFunction(bookingFunction)
                .resultPath("$")
                .build())
                .next(bookingRoomFailed);

        var confirmRoomReservationTask = new LambdaInvoke(this, "ConfirmRoomReservation", LambdaInvokeProps.builder()
                .lambdaFunction(bookingFunction)
                .resultPath("$")
                .build());

        var processPaymentTask = new LambdaInvoke(this, "ProcessPayment", LambdaInvokeProps.builder()
                .lambdaFunction(paymentsFunction)
                .resultPath("$")
                .build())
                .addCatch(cancelRoomReservationTask);

        var reserveRoomTask = new LambdaInvoke(this, "ReserveRoom", LambdaInvokeProps.builder()
                    .lambdaFunction(bookingFunction)
                    .resultPath("$")
                    .build())
                .addCatch(bookingRoomFailed);

        var sagaDefinition = Chain.start(reserveRoomTask)
                .next(processPaymentTask)
                .next(confirmRoomReservationTask)
                .next(bookingRoomSuccess);

        var saga = new StateMachine(this, "StateMachine", StateMachineProps.builder()
                .definition(sagaDefinition)
                .build());

        var sagaFunction = createLambda("reserve-room-saga",
                "com.hotelx.sfn.boundary.FunctionHandler::handle",
                "../saga-lambda/target/function.jar",
                Map.of("statemachine_arn", saga.getStateMachineArn()));

        saga.grantStartExecution(sagaFunction);
    }

    Function createLambda(String functionName, String handler, String jarFileLocation, Map<String, String> envVariables) {
        var function = Function.Builder.create(this, functionName)
                .code(Code.fromAsset(jarFileLocation))
                .functionName(functionName)
                .memorySize(MEMORY_SIZE)
                .timeout(FUNCTION_TIMEOUT)
                .handler(handler)
                .runtime(Runtime.JAVA_11)
                .environment(envVariables)
                .build();

        return function;
    }

    Table createDdbTable(String tableName) {
        return Table.Builder.create(this, tableName)
                .billingMode(BillingMode.PROVISIONED)
                .readCapacity(1)
                .writeCapacity(1)
                .removalPolicy(RemovalPolicy.DESTROY)
                .partitionKey(Attribute.builder()
                        .name("roomId")
                        .type(AttributeType.STRING)
                        .build())
                .build();
    }

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
