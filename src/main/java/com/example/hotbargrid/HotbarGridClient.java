package com.example.hotbargrid;

import com.example.hotbargrid.mixin.InventoryAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class HotbarGridClient implements ClientModInitializer {

    // Vanilla GUI texture atlas sprites (using Identifier in 1.21.11)
    private static final Identifier HOTBAR_SLOT = Identifier.withDefaultNamespace("hud/hotbar_slot");
    private static final Identifier HOTBAR_SELECTION = Identifier.withDefaultNamespace("hud/hotbar_selection");
    private static final Identifier HOTBAR_OFFHAND_SLOT = Identifier.withDefaultNamespace("hud/hotbar_offhand_left");

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((guiGraphics, tickCounter) -> {
            Minecraft client = Minecraft.getInstance();
            LocalPlayer player = client.player;
            if (player == null) return;

            renderGrid(guiGraphics, client, player);
            renderOffhand(guiGraphics, client, player);
        });
    }

    // -------------------------------------------------------------------------
    // 3x3 hotbar grid (right side)
    // -------------------------------------------------------------------------

    private static void renderGrid(GuiGraphics graphics, Minecraft client, LocalPlayer player) {
        Inventory inv = player.getInventory();
        int selectedSlot = ((InventoryAccessor) inv).getSelectedSlot();

        int screenWidth  = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        // Vanilla hotbar slot is 22x22 pixels (20x20 interior + 1px border on each side)
        int slotSize = 22;
        int gap = 2;

        int gridWidth  = slotSize * 3 + gap * 2;
        int gridHeight = slotSize * 3 + gap * 2;

        int margin = 8;

        // Anchor just to the right of the vanilla hotbar area
        int barsRightEdge = screenWidth / 2 + 91;
        int startX = barsRightEdge + margin;

        int bottomPadding = 4;
        int startY = screenHeight - gridHeight - bottomPadding;

        // Share geometry with the offhand renderer
        HotbarGridState.GRID_SLOT_SIZE      = slotSize;
        HotbarGridState.GRID_HEIGHT         = gridHeight;
        HotbarGridState.GRID_BOTTOM_PADDING = bottomPadding;

        for (int i = 0; i < 9; i++) {
            int col = i % 3;
            int row = i / 3;
            int x = startX + col * (slotSize + gap);
            int y = startY + row * (slotSize + gap);

            // Draw vanilla slot background texture (RenderPipeline added in 1.21.11)
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SLOT, x, y, slotSize, slotSize);

            // Draw selection highlight if this is the active slot
            if (i == selectedSlot) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SELECTION, x - 2, y - 2, 26, 26);
            }

            ItemStack stack = inv.getItem(i);

            // Items render at 16x16, centered in the 22x22 slot
            int itemX = x + 3;
            int itemY = y + 3;

            graphics.renderItem(stack, itemX, itemY);
            graphics.renderItemDecorations(client.font, stack, itemX, itemY);
        }
    }

    // -------------------------------------------------------------------------
    // Offhand slot (left side, mirrored from grid)
    // -------------------------------------------------------------------------

    private static void renderOffhand(GuiGraphics graphics, Minecraft client, LocalPlayer player) {
        ItemStack offhand = player.getOffhandItem();

        int screenWidth  = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        int slotSize   = HotbarGridState.GRID_SLOT_SIZE;
        int gridHeight = HotbarGridState.GRID_HEIGHT;
        int bottomPad  = HotbarGridState.GRID_BOTTOM_PADDING;

        if (slotSize == 0) return; // grid hasn't rendered yet this frame

        // Mirror: same distance to the left of the hotbar as the grid is to the right
        int margin       = 8;
        int barsLeftEdge = screenWidth / 2 - 91;
        int x = barsLeftEdge - margin - slotSize;

        // Vertically center within the grid's height band
        int y = screenHeight - gridHeight - bottomPad + (gridHeight - slotSize) / 2;

        // Draw vanilla offhand slot texture
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_SLOT, x, y, slotSize, slotSize);

        // Item
        int itemX = x + 3;
        int itemY = y + 3;

        graphics.renderItem(offhand, itemX, itemY);
        graphics.renderItemDecorations(client.font, offhand, itemX, itemY);
    }
}
