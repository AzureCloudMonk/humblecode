package com.humblecode.humblecode.rest;

import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PaymentControl {

    private static final Logger logger = LoggerFactory.getLogger(PaymentControl.class);

    @Value("${app.stripe.secret}")
    String secret;

    @PostMapping(value = "/pay", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public Mono<String> postCharge(@RequestBody Map params) {
        // Set your secret key: remember to change this to your live secret key in production
        // See your keys here: https://dashboard.stripe.com/account/apikeys
        Stripe.apiKey = secret;

        /*Map<String, Object> params = new HashMap<>();
        params.put("amount", 999);
        params.put("currency", "usd");
        params.put("source", "tok_visa");
        params.put("receipt_email", "jenny.rosen@example.com");*/
        return Mono.<String> create(callback -> {
            try {
                Charge charge = Charge.create(params);

                logger.info("Charge:" + charge.getDescription() + " at " +
                        System.currentTimeMillis() + " of amount: " + charge.getAmount() +
                        " was " + charge.getStatus());

                callback.success(charge.getStatus());
            } catch (Throwable e) {
                callback.error(e);
            }
        }).doOnError(e -> logger.error(e.getMessage(), e));
    }

}
