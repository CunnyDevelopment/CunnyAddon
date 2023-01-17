package io.github.cunnydevelopment.cunnyaddon.utility.blocks;

import io.github.cunnydevelopment.cunnyaddon.utility.PacketUtils;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BlockHandling {
    private static final List<BlockPos> antiGhostBlocks = new ArrayList<>();
    private static BlockPos pos = BlockPos.ORIGIN;
    private final List<PlayerActionC2SPacket.Action> actions = List.of(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK);

    public static void addGhostBlock(BlockPos blockPos) {
        if (!antiGhostBlocks.contains(blockPos)) antiGhostBlocks.add(blockPos);
    }

    public static void instantBreak(BlockPos blockPos) {
        assert mc.player != null;
        if (blockPos != pos) {
            swingHand();
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, Direction.DOWN));
        }
        swingHand();
        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, Direction.DOWN));
    }

    public static void swingHand() {
        assert mc.player != null;
        if (!mc.player.handSwinging) PacketUtils.send(new HandSwingC2SPacket(Hand.MAIN_HAND));
    }

    @EventHandler
    private void onPacketSent(PacketEvent.Sent event) {
        if (event.packet instanceof PlayerActionC2SPacket packet) {
            if (actions.contains(packet.getAction())) {
                pos = packet.getPos();
            }
        }

        if (event.packet instanceof PlayerInteractBlockC2SPacket packet) {
            BlockPos blockPos = packet.getBlockHitResult().getBlockPos();
            if (antiGhostBlocks.contains(blockPos)) {
                assert mc.player != null;
                mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, Direction.DOWN));
                mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, blockPos, Direction.DOWN));
                antiGhostBlocks.remove(blockPos);
            }
        }
    }
}
