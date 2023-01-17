package io.github.cunnydevelopment.cunnyaddon.utility;

import meteordevelopment.meteorclient.MeteorClient;

public class UtilityEvent {
    public UtilityEvent() {
        MeteorClient.EVENT_BUS.subscribe(this);
    }
}
