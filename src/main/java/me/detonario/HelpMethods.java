package me.detonario;

import java.awt.geom.Rectangle2D;

public class HelpMethods {

    private static final Player player = Game.getInstance().getPlayer();


    public static boolean canMoveHere(float x, float y, float width, float height, int[][] lvlData) {
        if (!isSolid(x, y, lvlData) && !isSolid(x + width, y + height - 2, lvlData) && !isSolid(x + width, y, lvlData) && !isSolid(x, y + height - 2, lvlData))
            return true;

        return false;
    }


    private static boolean isSolid(float x, float y, int[][] lvlData) {
        if (x < 0 || x > 5000) return true;
        //if (y < 0 || y > 450) return true;

        float xIndex = x / 32;
        float yIndex = y / 32;

        int value = lvlData[(int) yIndex][(int) xIndex];

        if (value == 1 || value == 2 || value == 4) return true;

        return false;
    }


    public static float getTileXPos(Rectangle2D.Float bounds, float vx) {

        int currentTile = (int) (bounds.x / 32);

        if (vx > 0) {
            //Right
            int tileXPos = currentTile * 32;
            int xOffset = (int) (32 - bounds.width);

            return tileXPos + xOffset - 1;

        } else if (vx < 0) {
            //Left
            return currentTile * 32;

        } else {
            return bounds.x;
        }
    }


    public static float getTileYPos(Rectangle2D.Float bounds, float airSpeed) {

        int currentTile = (int) (bounds.y / 32);

        if (airSpeed > 0) {
            //Falling - touching floor
            int tileYPos = currentTile * 32;
            int yOffset = (int) (32 - bounds.height);

            return tileYPos + yOffset - 1;

        } else if (airSpeed < 0) {
            //Jumping
            return currentTile * 32;

        } else {
            return bounds.y;
        }
    }


    public static boolean isEntityOnFloor(Rectangle2D.Float bounds, int[][] lvlData) {
        // Check the pixel below bottomleft and bottomright
        if (!isSolid(bounds.x, bounds.y + bounds.height + 1, lvlData))
            if (!isSolid(bounds.x + bounds.width, bounds.y + bounds.height + 1, lvlData))
                return false;

        return true;
    }
}