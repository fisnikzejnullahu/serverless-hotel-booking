package com.hotelx.sfn.boundary;

import jakarta.json.Json;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest;

import java.util.UUID;

/**
 * @author Fisnik Zejnullahu
 */
public class FunctionHandler {

    private final SfnClient sfn;
    private final String stateMachineArn;

    public FunctionHandler() {
        this.sfn = SfnClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();

        this.stateMachineArn = System.getenv("statemachine_arn");
    }

    public String handle(Object event) {
        var input = Json.createObjectBuilder()
                .add("roomId", UUID.randomUUID().toString())
                .add("StartReservation", true)
                .build()
                .toString();

        sfn.startExecution(StartExecutionRequest.builder()
                .stateMachineArn(this.stateMachineArn)
                .input(input)
                .build());

        return "started execution!!!!";
    }
}
