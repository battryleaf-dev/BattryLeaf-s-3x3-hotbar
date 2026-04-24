package com.example.hotbargrid.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class HotbarMixin {

    /**
     * Cancels the entire vanilla hotbar render.
     * Our HotbarGridClient renders the 3x3 grid and offhand slot instead.
     */
    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void cancelVanillaHotbar(GuiGraphics graphics, float tickDelta, CallbackInfo ci) {
        // Cancel vanilla hotbar rendering
        ci.cancel();
    }
}