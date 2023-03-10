package io.github.cunnydevelopment.cunnyaddon.utility.placeholders;

import java.text.ParseException;

public class ParseVariable {

    private final String name;
    private final ParseMethod parser;

    public ParseVariable(String name, ParseMethod parser) {
        this.name = name;
        this.parser = parser;
    }

    public String get(String... args) {
        try {
            return parser.getValue(args);
        } catch (ParseException e) {
            return "ERROR";
        }
    }

    public String name() {
        return name;
    }
}
