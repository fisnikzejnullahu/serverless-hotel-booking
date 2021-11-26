package com.hotelx.payments.boundary;

import com.hotelx.payments.control.PaymentsService;

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

    public String handle(Object event) {
        System.out.println("Handling: " + event.getClass().getName());
        return "Hi from payments service - " + System.currentTimeMillis();
    }
}
