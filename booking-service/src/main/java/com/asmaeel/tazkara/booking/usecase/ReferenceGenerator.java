package com.asmaeel.tazkara.booking.usecase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ReferenceGenerator {

    ///
    /// حسب معادلة بقلك انه لو عندك 100 مليون رقم احتمال التكرار 0.000425%
    ///
    /// فاذا طلع معك ايرور وبقلك مشكلة في السيرفر حظك كثير حلو صديقي
    ///

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

    @Value("${reference.random.size}")
    private int SIZE;

    public String execute() {
        StringBuilder reference = new StringBuilder("TZK-");

        for (int i = 0; i < SIZE; i++) {
            reference.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }

        return reference.toString();
    }
}