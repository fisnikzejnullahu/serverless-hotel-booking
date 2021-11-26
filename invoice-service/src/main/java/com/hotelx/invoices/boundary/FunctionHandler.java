package com.hotelx.invoices.boundary;

/**
 * @author Fisnik Zejnullahu
 */
public class FunctionHandler {

    public FunctionHandler() {
        System.out.println("Initializing FunctionHandler...");
    }

    public String handle(Object event) {
        System.out.println("Handling: " + event.getClass().getName());
        return "Hi from invoice service - " + System.currentTimeMillis();
    }
}
