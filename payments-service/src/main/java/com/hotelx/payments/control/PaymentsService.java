package com.hotelx.payments.control;

import com.hotelx.payments.entity.Payment;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Fisnik Zejnullahu
 */
public class PaymentsService {

    private final String TABLE_NAME;
    private final DynamoDbEnhancedClient ENHANCED_DDB_CLIENT;

    public PaymentsService(String dynamoDbTableName) {
        this.TABLE_NAME = dynamoDbTableName;

        var ddb = DynamoDbClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();

        this.ENHANCED_DDB_CLIENT = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();
    }

    public String processPayment(double amount, String roomNumber) {
        try {
            Payment payment = new Payment(UUID.randomUUID().toString(), amount, "CONFIRMED", roomNumber, Instant.now());
            putPaymentItemInDd(payment);
            return payment.getId();
        } catch (DynamoDbException ex) {
            // do something with exception
            throw ex;
        }
    }

    private void putPaymentItemInDd(Payment payment) {
        DynamoDbTable<Payment> roomDdbTable = ENHANCED_DDB_CLIENT.table(TABLE_NAME, TableSchema.fromBean(Payment.class));
        // Put the customer data into a DynamoDB table
        roomDdbTable.putItem(payment);
        System.out.println("Item put success!");
    }

    public Payment getPayment(String id) {
        try {
            DynamoDbTable<Payment> mappedTable = ENHANCED_DDB_CLIENT.table(TABLE_NAME, TableSchema.fromBean(Payment.class));

            Key key = Key.builder()
                    .partitionValue(id)
                    .build();

            return mappedTable.getItem(r -> r.key(key));
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }
}
