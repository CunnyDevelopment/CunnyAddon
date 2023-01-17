package io.github.cunnydevelopment.cunnyaddon.utility;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PacketUtils {
    public static void send(Packet<?> packet) {
        if (mc.getNetworkHandler() == null) return;
        mc.getNetworkHandler().getConnection().setState(NetworkState.getPacketHandlerState(packet));
        mc.getNetworkHandler().getConnection().channel.writeAndFlush(packet);
    }

    public static void send(ClientConnection connection, Packet<?> packet) {
        if (connection == null) return;
        connection.setState(NetworkState.getPacketHandlerState(packet));
        connection.channel.writeAndFlush(packet);
    }

    public static void chat(String string, boolean command) {
        if (command) command(string);
        else chat(string);
    }

    public static void chat(String string) {
        if (ServerPlayNetworkHandler.hasIllegalCharacter(string) || mc.getNetworkHandler() == null) {
            return;
        }

        if (string.startsWith("/")) {
            command(string.replaceFirst("/", ""));
        } else {
            mc.getNetworkHandler().sendChatMessage(string);
        }
    }

    public static void command(String string) {
        if (ServerPlayNetworkHandler.hasIllegalCharacter(string) || mc.getNetworkHandler() == null) {
            return;
        }
        mc.getNetworkHandler().sendChatCommand(string);
    }
}

