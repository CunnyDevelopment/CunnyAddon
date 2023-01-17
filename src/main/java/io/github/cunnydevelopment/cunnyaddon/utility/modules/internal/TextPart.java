package io.github.cunnydevelopment.cunnyaddon.utility.modules.internal;

import meteordevelopment.meteorclient.utils.render.color.Color;

public class TextPart {
    public String text;
    public Color color = Color.WHITE;

    public TextPart(String text) {
        this.text = text;
    }

    public TextPart(String text, Color color) {
        this.text = text;
        this.color = color;
    }
}
