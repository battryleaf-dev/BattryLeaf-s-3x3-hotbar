package com.example.hotbargrid;

/**
 * Shared render-frame state written by the grid renderer and read by the offhand renderer.
 * All fields are set every frame before renderOffhand is called, so stale values are never used.
 */
public class HotbarGridState {
    private HotbarGridState() {}

    /** Slot size in pixels (scales with screen height). */
    public static int GRID_SLOT_SIZE      = 0;

    /** Total pixel height of the 3x3 grid including gaps. */
    public static int GRID_HEIGHT         = 0;

    /** Bottom padding below the grid. */
    public static int GRID_BOTTOM_PADDING = 0;

    // GRID_BOTTOM_Y kept for backwards compat in case external code references it
    public static int GRID_BOTTOM_Y       = 0;
}