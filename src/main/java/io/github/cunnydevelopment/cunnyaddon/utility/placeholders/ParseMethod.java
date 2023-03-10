package io.github.cunnydevelopment.cunnyaddon.utility.placeholders;

import java.text.ParseException;

@FunctionalInterface
public interface ParseMethod {
    String getValue(String... args) throws ParseException;
}
