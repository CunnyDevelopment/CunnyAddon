package io.github.cunnydevelopment.cunnyaddon.utility;

import io.github.cunnydevelopment.cunnyaddon.events.MidTickEvent;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;

import java.util.Timer;
import java.util.TimerTask;

public class MidTickHandler extends UtilityEvent {
    private final Timer timer = new Timer();

    @EventHandler(priority = 1000000)
    public void onTick(TickEvent.Pre event) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MeteorClient.EVENT_BUS.post(MidTickEvent.get());
            }
        }, 25);
    }
}
