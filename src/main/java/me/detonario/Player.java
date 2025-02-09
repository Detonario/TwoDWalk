package me.detonario;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Player implements KeyListener {

    private final Rectangle2D.Float bounds;
    private int health;

    private int playerFrame = 0;
    private long lastPlayerFrameTime = System.currentTimeMillis();

    private final int imageAirSpaceX = 12;
    private final int imageAirSpaceY = 16;

    public boolean up, down, left, right, jump;

    private boolean leftWalk = false;

    //Moving, Jumping and Gravity
    public float vx;
    public float airSpeed = 0; // vy
    private float gravity = 0.42f;
    private float jumpSpeed = -8.25f;
    private float fallSpeedAfterHeadBang = 0.5f;
    private boolean inAir = false;

    public Player(int x, int y) {
        this.bounds = new Rectangle2D.Float(x, y, 25, 27);
        this.health = 100;
    }


    public void handleInput() {
        if (!HelpMethods.isEntityOnFloor(bounds, Game.getInstance().getLevelOne())) {
            inAir = true;
        }

        if (jump) {
            jump();
        }

        if (!left && !right && !inAir) return;

        vx = 0;

        if (left) vx -= 3;
        if (right) vx += 3;

        if (inAir) {
            updateYPos();
        } else {
            updateXPos(vx);
        }
    }


    private void jump() {
        if (inAir) return;
        inAir = true;
        airSpeed = jumpSpeed;
    }

    private void resetAirValue() {
        inAir = false;
        airSpeed = 0;
    }


    private void updateXPos(float vx) {
        if (HelpMethods.canMoveHere(bounds.x + vx, bounds.y, bounds.width, bounds.height, Game.getInstance().getLevelOne())) {
            bounds.x += vx;
        } else {
            bounds.x = HelpMethods.getTileXPos(bounds, vx);
        }
    }

    private void updateYPos() {
        if (HelpMethods.canMoveHere(bounds.x, bounds.y + airSpeed, bounds.width, bounds.height, Game.getInstance().getLevelOne())) {
            airSpeed += gravity;
            bounds.y += airSpeed;

            updateXPos(vx);
        } else {
            bounds.y = HelpMethods.getTileYPos(bounds, airSpeed);

            if (airSpeed > 0) {
                resetAirValue();
            } else airSpeed = fallSpeedAfterHeadBang;

            updateXPos(vx);
        }
    }


    public void drawPlayer(Graphics2D g2d) {
        if (System.currentTimeMillis() - lastPlayerFrameTime >= 100) {
            playerFrame = (playerFrame + 1) % 8;
            lastPlayerFrameTime = System.currentTimeMillis();
        }


        // Idle
        if (!left && !right && !inAir && !leftWalk) {
            BufferedImage idleRight = Game.getInstance().getPlayerAtlas().getSubimage(0, 0, 52, 44);
            g2d.drawImage(idleRight, (int) bounds.x - imageAirSpaceX, (int) bounds.y - imageAirSpaceY, 52, 44, null);
        } else if (!left && !right && !inAir) {
            BufferedImage idleLeft = Game.getInstance().getPlayerAtlas().getSubimage(0, 0, 52, 44);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.scale(-1, 1);
            g2d.drawImage(idleLeft, (int) -bounds.x - imageAirSpaceX - 26, (int) bounds.y - imageAirSpaceY, 52, 44, null);
            g2d.scale(-1, 1);
        }


        // Only jumping
        if (!left && !right && airSpeed < 0) {
            BufferedImage idle = Game.getInstance().getPlayerAtlas().getSubimage(52 * 1, 44 * 0, 52, 44);
            g2d.drawImage(idle, (int) bounds.x - imageAirSpaceX, (int) bounds.y - imageAirSpaceY, 52, 44, null);
        } else if (!left && !right && airSpeed > 0) {
            BufferedImage idle = Game.getInstance().getPlayerAtlas().getSubimage(52 * 2, 44 * 0, 52, 44);
            g2d.drawImage(idle, (int) bounds.x - imageAirSpaceX, (int) bounds.y - imageAirSpaceY, 52, 44, null);
        }


        // Only moving
        if (right && !inAir) {
            leftWalk = false;
            BufferedImage right = Game.getInstance().getPlayerAtlas().getSubimage(playerFrame * 52, 44, 52, 44);
            g2d.drawImage(right, (int) bounds.x - imageAirSpaceX, (int) bounds.y - imageAirSpaceY, 52, 44, null);
        } else if (left && !inAir) {
            leftWalk = true;
            BufferedImage left = Game.getInstance().getPlayerAtlas().getSubimage(playerFrame * 52, 44, 52, 44);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.scale(-1, 1);
            g2d.drawImage(left, (int) -bounds.x - imageAirSpaceX - 26, (int) bounds.y - imageAirSpaceY, 52, 44, null);
            g2d.scale(-1, 1);
        }


        // Jumping AND moving
        if (inAir && right && airSpeed < 0) {
            leftWalk = false;
            BufferedImage rightJump = Game.getInstance().getPlayerAtlas().getSubimage(52 * 1, 44 * 0, 52, 44);
            g2d.drawImage(rightJump, (int) bounds.x - imageAirSpaceX, (int) bounds.y - imageAirSpaceY, 52, 44, null);
        } else if (inAir && right && airSpeed > 0) {
            leftWalk = false;
            BufferedImage rightFall = Game.getInstance().getPlayerAtlas().getSubimage(52 * 2, 44 * 0, 52, 44);
            g2d.drawImage(rightFall, (int) bounds.x - imageAirSpaceX, (int) bounds.y - imageAirSpaceY, 52, 44, null);
        } else if (inAir && left && airSpeed < 0) {
            leftWalk = true;
            BufferedImage leftJump = Game.getInstance().getPlayerAtlas().getSubimage(52 * 1, 44 * 0, 52, 44);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.scale(-1, 1);
            g2d.drawImage(leftJump, (int) -bounds.x - imageAirSpaceX - 26, (int) bounds.y - imageAirSpaceY, 52, 44, null);
            g2d.scale(-1, 1);
        } else if (inAir && left && airSpeed > 0) {
            leftWalk = true;
            BufferedImage leftFall = Game.getInstance().getPlayerAtlas().getSubimage(52 * 2, 44 * 0, 52, 44);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.scale(-1, 1);
            g2d.drawImage(leftFall, (int) -bounds.x - imageAirSpaceX - 26, (int) bounds.y - imageAirSpaceY, 52, 44, null);
            g2d.scale(-1, 1);
        }


    }


    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W) up = true;
        if (key == KeyEvent.VK_S) down = true;
        if (key == KeyEvent.VK_A) left = true;
        if (key == KeyEvent.VK_D) right = true;

        if (key == KeyEvent.VK_SPACE) jump = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W) up = false;
        if (key == KeyEvent.VK_S) down = false;
        if (key == KeyEvent.VK_A) left = false;
        if (key == KeyEvent.VK_D) right = false;

        if (key == KeyEvent.VK_SPACE) jump = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }


    public void setX(int x) {
        this.bounds.x = x;
    }

    public void setY(int y) {
        this.bounds.y = y;
    }


    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }


    public Rectangle2D.Float getBounds() {
        return bounds;
    }
}
