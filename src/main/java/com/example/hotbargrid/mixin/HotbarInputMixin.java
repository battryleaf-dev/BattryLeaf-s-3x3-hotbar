package com.example.hotbargrid.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class HotbarInputMixin {

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        if (client.screen != null) return;

        Inventory inv = client.player.getInventory();
        InventoryAccessor accessor = (InventoryAccessor) inv;
        int slot = accessor.getSelectedSlot();

        // Scroll up → previous slot (decrease), scroll down → next slot (increase)
        if (vertical > 0) {
            slot = (slot + 8) % 9; // equivalent to slot - 1 with wrapping
        } else if (vertical < 0) {
            slot = (slot + 1) % 9;
        } else {
            return;
        }

        accessor.setSelectedSlot(slot);
        ci.cancel();
    }
}