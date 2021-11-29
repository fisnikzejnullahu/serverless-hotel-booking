package com.hotelx.payments.boundary;

import com.hotelx.payments.control.PaymentsService;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

import java.io.StringReader;
import java.util.Map;
import java.util.Random;

/**
 * @author Fisnik Zejnullahu
 */
public class FunctionHandler {

    private final PaymentsService paymentsService;

    public FunctionHandler() {
        System.out.println("Initializing FunctionHandler...");
        String tableName = System.getenv("DDB_TABLE_NAME");
        if (tableName == null) {
            throw new RuntimeException("No DynamoDb table name specified!");
        }

        this.paymentsService = new PaymentsService(tableName);
    }

    public String handle(Map<String, Object> event) {
        JsonObject eventJson = Json.createObjectBuilder(event).build();

        JsonObjectBuilder responseJsonBuild = Json.createObjectBuilder();
        var paymentMade = false;
        String roomId = null;
        String type = null;

        JsonObject payload = Json.createReader(new StringReader(eventJson.getString("Payload"))).readObject();

        if (payload.getString("type").equals("ReserveRoomResult")) {
            roomId = payload.getString("roomId");
            paymentsService.processPayment(new Random().nextInt(100), roomId);
            paymentMade = true;
        }

        return responseJsonBuild
                .add("type", "ProcessPaymentResult")
                .add("paymentMade", paymentMade)
                .add("roomId", roomId)
                .build()
                .toString();
    }
}
