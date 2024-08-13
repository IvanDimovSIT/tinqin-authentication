package com.tinqinacademy.authentication.core.util;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class RandomCodeGenerator {
    private static final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final Random rand = new Random();

    public String generate(int length) {
        if(length<0){
            throw new IllegalArgumentException("Length must be greater than zero");
        }

        return IntStream
                .range(0, length)
                .mapToObj(i -> String.valueOf(CODE_CHARACTERS.charAt(rand.nextInt(CODE_CHARACTERS.length()))))
                .collect(Collectors.joining());
    }
}
