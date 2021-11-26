package it;

import com.hotelx.payments.control.PaymentsService;
import com.hotelx.payments.entity.Payment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Fisnik Zejnullahu
 */
public class PaymentsServiceIT {

    private PaymentsService paymentsService;

    @Before
    public void init() {
        this.paymentsService = new PaymentsService("Payments");
    }

    @Test
    public void testPaymentInsert() {
        double amount = 100.99;
        String paymentId = this.paymentsService.processPayment(amount, "100");
        System.out.println("paymentId = " + paymentId);
        Payment payment = this.paymentsService.getPayment(paymentId);
        assertEquals(amount, payment.getAmount(), 0.1);
    }
}