package me.detonario;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        Game game = Game.getInstance();

        game.start();
        game.update();

    }
}