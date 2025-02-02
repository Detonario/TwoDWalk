package me.detonario;

public class Main {

    public static void main(String[] args) {

        Game game;

        try {
            game = Game.getInstance();

            game.start();
            game.update();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}