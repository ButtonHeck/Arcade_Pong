package com.buttonHeck.pong.util;

public class Options {

    private static Difficulty difficulty = Difficulty.MEDIUM;
    private static boolean itemsOn = true;

    public static boolean itemsOn() {
        return itemsOn;
    }

    public static void setItemsOn(boolean on) {
        itemsOn = on;
    }


    public enum Difficulty {
        EASY(-1), MEDIUM(0), HARD(1);

        public final int opponentUpdateSpeedUp;

        Difficulty(int i) {
            opponentUpdateSpeedUp = i;
        }
    }

    public static Difficulty getDifficulty() {
        return difficulty;
    }

    public static void setDifficulty(Difficulty difficulty) {
        Options.difficulty = difficulty;
    }
}
