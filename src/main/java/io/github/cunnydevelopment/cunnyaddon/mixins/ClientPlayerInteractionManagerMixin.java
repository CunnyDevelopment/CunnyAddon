package io.github.cunnydevelopment.cunnyaddon.mixins;

import io.github.cunnydevelopment.cunnyaddon.modules.misc.PacketPlace;
import io.github.cunnydevelopment.cunnyaddon.utility.InventoryUtils;
import io.github.cunnydevelopment.cunnyaddon.utility.blocks.BlockUtils;
import io.github.cunnydevelopment.cunnyaddon.utility.modules.external.ModuleReference;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    public void interactBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (!ModuleReference.state(PacketPlace.class)) return;
        if (BlockUtils.canUse(hitResult.getBlockPos()) && !mc.player.isSneaking()) return;
        InventoryUtils.syncHand();
        assert mc.player != null;
        Objects.requireNonNull(mc.player.networkHandler).sendPacket(new HandSwingC2SPacket(hand));
        Objects.requireNonNull(mc.player.networkHandler).sendPacket(new PlayerInteractBlockC2SPacket(hand, hitResult, 0));
        cir.setReturnValue(ActionResult.PASS);
    }
}
