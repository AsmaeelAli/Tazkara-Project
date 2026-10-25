package com.asmaeel.tazkara.identity.common;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class Normalizer {

    public String normalize(String text) {
        return text == null
                ? null
                : text.trim().toLowerCase(Locale.ROOT);
    }
}
