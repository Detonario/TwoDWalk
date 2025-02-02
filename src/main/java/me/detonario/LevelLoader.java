package me.detonario;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {

    private static final LevelLoader instance = new LevelLoader();

    private LevelLoader() {
    }

    private int[][] level;

    public void loadLevel(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            List<int[]> levelList = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("");

                int[] row = new int[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    row[i] = Integer.parseInt(tokens[i]);
                }

                levelList.add(row);
            }


            level = new int[levelList.size()][];
            for (int i = 0; i < levelList.size(); i++) {

                level[i] = levelList.get(i);

                /*
                //
                //
                //             //
                //               //
                //                 //
                //////////////////////
                                  //
                                //
                              */

            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }


    }

    public int getAmountTiles() {
        int totalElements = 0;

        for (int i = 0; i < level.length; i++) {
            totalElements += level[i].length;
        }

        return totalElements;
    }

    public int[][] getLevel() {
        return level;
    }

    public static LevelLoader getInstance() {
        return instance;
    }

}
