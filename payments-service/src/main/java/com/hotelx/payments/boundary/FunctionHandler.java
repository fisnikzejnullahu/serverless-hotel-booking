package com.hotelx.payments.boundary;

/**
 * @author Fisnik Zejnullahu
 */
public class FunctionHandler {

    public String handle(Object event) {
        System.out.println("Handling: " + event.getClass().getName());
        return "Hi from payments service - " + System.currentTimeMillis();
    }
}
