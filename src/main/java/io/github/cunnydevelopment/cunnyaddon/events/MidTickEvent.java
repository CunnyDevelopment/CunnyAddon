package io.github.cunnydevelopment.cunnyaddon.events;

public class MidTickEvent {
    public static MidTickEvent INSTANCE = new MidTickEvent();

    public static MidTickEvent get() {
        return INSTANCE;
    }
}
