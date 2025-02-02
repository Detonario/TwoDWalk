package me.detonario;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Player implements KeyListener {

    private final Rectangle2D.Float bounds;
    private int health;

    private float vx;
    public boolean up, down, left, right, jump;

    private int playerFrame = 0;
    private long lastPlayerFrameTime = System.currentTimeMillis();

    //Jumping and Gravity
    private float airSpeed = 0;
    private float gravity = 0.42f;
    private float jumpSpeed = -8.25f;
    private float fallSpeedAfterHeadBang = 0.5f;
    private boolean inAir = false;

    //public int jumpYPos;
    //public int landYPos;

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
            //jumpYPos = (int) bounds.y;
        }

        if (!left && !right && !inAir) return;

        vx = 0;

        if (left) vx -= 3;
        if (right) vx += 3;


        if (inAir) {
            if (HelpMethods.canMoveHere(bounds.x, bounds.y + airSpeed, bounds.width, bounds.height, Game.getInstance().getLevelOne())) {
                airSpeed += gravity;
                bounds.y += airSpeed;

                updateXPos(vx);
            } else {
                bounds.y = HelpMethods.getEntityYPosUnderRoofOrAboveFloor(bounds, airSpeed); // HERE
                if (airSpeed > 0) {
                    resetInAir();
                    //landYPos = (int) bounds.y;
                } else airSpeed = fallSpeedAfterHeadBang;

                updateXPos(vx);
            }

        } else {
            updateXPos(vx);
        }


    }


    private void jump() {
        if (inAir) return;
        inAir = true;
        airSpeed = jumpSpeed;
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    private void updateXPos(float vx) {
        if (HelpMethods.canMoveHere(bounds.x + vx, bounds.y, bounds.width, bounds.height, Game.getInstance().getLevelOne())) {
            bounds.x += vx;
        } else {
            System.out.println("Blockiert! Vorher: " + bounds.x + ", vx: " + vx);
            bounds.x = HelpMethods.getEntityXPosNextToWall(bounds, vx);
            System.out.println("Nachher: " + bounds.x);
        }
    }


    public void drawPlayer(Graphics2D g2d) {
        if (System.currentTimeMillis() - lastPlayerFrameTime >= 100) {
            playerFrame = (playerFrame + 1) % 8;
            lastPlayerFrameTime = System.currentTimeMillis();
        }

        if (!left && !right && !inAir) {
            BufferedImage idle = Game.getInstance().getPlayerAtlas().getSubimage(0, 0, 52, 44);
            g2d.drawImage(idle, (int) bounds.x - 12, (int) bounds.y - 16, 52, 44, null);
        }

        if (left) {
            BufferedImage left = Game.getInstance().getPlayerAtlas().getSubimage(playerFrame * 52, 44, 52, 44);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.scale(-1, 1);
            g2d.drawImage(left, (int) -bounds.x - 12 - 26, (int) bounds.y - 16, 52, 44, null);
            g2d.scale(-1, 1);
        } else if (right) {
            BufferedImage right = Game.getInstance().getPlayerAtlas().getSubimage(playerFrame * 52, 44, 52, 44);
            g2d.drawImage(right, (int) bounds.x - 12, (int) bounds.y - 16, 52, 44, null);
        }

        if (inAir && !left && !right && airSpeed < 0) {
            BufferedImage jump = Game.getInstance().getPlayerAtlas().getSubimage(52 * 1, 44 * 0, 52, 44);
            g2d.drawImage(jump, (int) bounds.x - 12, (int) bounds.y - 16, 52, 44, null);
        } else if (inAir && !left && !right && airSpeed > 0) {
            BufferedImage fall = Game.getInstance().getPlayerAtlas().getSubimage(52 * 2, 44 * 0, 52, 44);
            g2d.drawImage(fall, (int) bounds.x - 12, (int) bounds.y - 16, 52, 44, null);
        }




        /*g2d.setColor(Color.RED);
        g2d.drawRect((int) bounds.x, (int) bounds.y, 25, 28);*/
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


    public Rectangle2D.Float getBounds() {
        return bounds;
    }


    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }


    public void setX(int x) {
        this.bounds.x = x;
    }

    public void setY(int y) {
        this.bounds.y = y;
    }


}
