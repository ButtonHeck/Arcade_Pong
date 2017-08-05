package com.buttonHeck.pong.util;

import com.buttonHeck.pong.handler.AudioHandler;

public class Options {

    private static boolean musicOn = true, soundsOn = true, itemsOn = true;
    private static Difficulty difficulty = Difficulty.MEDIUM;

    public enum Difficulty {
        EASY(-1), MEDIUM(0), HARD(1);

        public final int opponentMoveDelaySpeedUp;

        Difficulty(int i) {
            opponentMoveDelaySpeedUp = i;
        }
    }

    //Getters and Setters

    public static Difficulty getDifficulty() {
        return difficulty;
    }

    public static void clickOnDifficultyCheckbox(Difficulty difficulty) {
        Options.difficulty = difficulty;
        AudioHandler.buttonClicked();
    }

    public static boolean isMusicOn() {
        return musicOn;
    }

    public static void setMusicOn(boolean musicOn) {
        Options.musicOn = musicOn;
    }

    public static boolean isSoundsOn() {
        return soundsOn;
    }

    public static void setSoundsOn(boolean soundsOn) {
        Options.soundsOn = soundsOn;
    }

    public static boolean isItemsOn() {
        return itemsOn;
    }

    public static void setItemsOn(boolean on) {
        itemsOn = on;
    }
}
