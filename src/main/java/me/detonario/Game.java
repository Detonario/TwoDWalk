package me.detonario;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Game extends Canvas {

    private static final Game instance = new Game();

    public static final int WIDTH = 860;
    public static final int HEIGHT = 485;

    private Player player;

    private BufferStrategy strategy;

    private BufferedImage backgroundImg, playerAtlas, tileAtlas;
    private List<BufferedImage> coinImgs = new ArrayList<>();

    private LevelLoader loader = LevelLoader.getInstance();
    private int[][] level1;

    private List<Rectangle> dirtList = new ArrayList<>();
    private List<Rectangle> grassList = new ArrayList<>();
    private List<Rectangle> coinList = new ArrayList<>();
    private List<Rectangle> tileList = new ArrayList<>();

    private JLabel scoreboard;
    private int coinsCount = 0;

    private int coinFrame = 0;
    private long lastCoinFrameTime = System.currentTimeMillis();

    private Game() {
    }


    public void start() throws IOException {
        player = new Player(400, 350);

        loadImages();
        //loadMusic();
        buildLevel1();
        createScoreboard();
        createFrame();

        setFocusable(true);
        addKeyListener(player);

        createBufferStrategy(2);
        strategy = getBufferStrategy();


        //System.out.println(LevelLoader.getInstance().getAmountTiles());
    }


    public void update() {

        while (true) {
            render();
            player.handleInput();

            Iterator<Rectangle> iterator = coinList.iterator();
            while (iterator.hasNext()) {
                Rectangle coin = iterator.next();
                if (player.getBounds().intersects(coin)) {
                    iterator.remove();
                    coinsCount++;
                }
            }

            scoreboard.setText(String.valueOf(coinsCount));


            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void render() {
        if (System.currentTimeMillis() - lastCoinFrameTime >= 100) {
            coinFrame = (coinFrame + 1) % 8;
            lastCoinFrameTime = System.currentTimeMillis();
        }

        Graphics g = strategy.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;
        g2d.clearRect(0, 0, getWidth(), getHeight());


        int cameraX = 0;
        if (player.getBounds().x >= 422) {
            cameraX = (int) (player.getBounds().x - (double) getWidth() / 2);
            g2d.translate(-cameraX, 0);
        }
        double parallaxX = cameraX * 0.7;
        g2d.drawImage(backgroundImg, (int) parallaxX, 0, null);


        player.drawPlayer(g2d);


        for (Rectangle dirt : dirtList) {
            g2d.drawImage(tileAtlas.getSubimage(0, 162, 32, 32), dirt.x, dirt.y, 32, 32, null);
        }

        for (Rectangle grass : grassList) {
            g2d.drawImage(tileAtlas.getSubimage(34, 162, 32, 32), grass.x, grass.y, 32, 32, null);
        }

        for (Rectangle coin : coinList) {
            g2d.drawImage(coinImgs.get(coinFrame).getSubimage(21, 21, 22, 22), coin.x + 4, coin.y + 4, 24, 24, null); //HERE
        }

        for (Rectangle tile : tileList) {
            g2d.drawImage(tileAtlas.getSubimage(194, 0, 32, 32), tile.x, tile.y, 32, 32, null);
            // benötigt Untersuchung der Pixel-Abstände in tileAtlas.png
        }


        g2d.dispose();
        strategy.show();
    }


    private void loadImages() throws IOException {
        backgroundImg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/img/background.png")));
        playerAtlas = ImageIO.read(Objects.requireNonNull(getClass().getResource("/img/playerAtlas.png")));
        tileAtlas = ImageIO.read(Objects.requireNonNull(getClass().getResource("/img/tileAtlas.png")));

        for (int i = 1; i <= 8; i++) {
            BufferedImage coin = ImageIO.read(Objects.requireNonNull(getClass().getResource("/img/coin/coin" + i + ".png")));
            coinImgs.add(coin);
        }
    }

    private void loadMusic() {
        try {
            InputStream musicFile = getClass().getResourceAsStream("/music/level1.wav");

            assert musicFile != null;
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            clip.start();

            //Thread.sleep(clip.getMicrosecondLength() / 1000);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void buildLevel1() {
        loader.loadLevel("src/main/resources/level/level1.txt");
        level1 = loader.getLevel();

        for (int y = 0; y < level1.length; y++) {
            for (int x = 0; x < level1[y].length; x++) {

                // an jeder Position eine eigene Grösse
                if (level1[y][x] == 1) {
                    Rectangle dirt = new Rectangle(x * 32, y * 32, 32, 32);
                    dirtList.add(dirt);
                }

                if (level1[y][x] == 2) {
                    Rectangle grass = new Rectangle(x * 32, y * 32, 32, 32);
                    grassList.add(grass);
                }

                if (level1[y][x] == 3) {
                    Rectangle coin = new Rectangle(x * 32, y * 32, 32, 32);
                    coinList.add(coin);
                }

                if (level1[y][x] == 4) {
                    Rectangle tile = new Rectangle(x * 32, y * 32, 32, 32);
                    tileList.add(tile);
                }
            }
        }
    }

    private void createScoreboard() {
        ImageIcon coinIcon = new ImageIcon(coinImgs.get(coinFrame));

        scoreboard = new JLabel();
        scoreboard.setIcon(coinIcon);
        scoreboard.setText(" " + coinsCount);
        scoreboard.setFont(new Font("Arial", Font.BOLD, 20));
        scoreboard.setBounds(10, 10, 100, 50);
        scoreboard.setVisible(true);
        scoreboard.setOpaque(true);
        scoreboard.setBackground(Color.GRAY);
    }

    private void createFrame() {
        JFrame frame = new JFrame();
        frame.add(scoreboard);
        frame.add(this);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setSize(WIDTH, HEIGHT);
        //frame.setLayout(null);
    }

    public BufferedImage mirrorImage(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        BufferedImage mirroredImage = new BufferedImage(width, height, original.getType());

        AffineTransform transform = new AffineTransform();
        transform.concatenate(AffineTransform.getScaleInstance(-1, 1)); // Horizontal spiegeln
        transform.concatenate(AffineTransform.getTranslateInstance(width, 0)); // Verschieben, um das Bild richtig auszurichten

        Graphics2D g2d = mirroredImage.createGraphics();
        g2d.drawImage(original, transform, null);
        g2d.dispose();

        return mirroredImage;
    }


    public BufferedImage getPlayerAtlas() {
        return playerAtlas;
    }

    public int[][] getLevelOne() {
        return level1;
    }

    public Player getPlayer() {
        return player;
    }

    public static Game getInstance() {
        return instance;
    }


}
