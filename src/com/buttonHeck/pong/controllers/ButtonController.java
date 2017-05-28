package com.buttonHeck.pong.controllers;

import com.buttonHeck.pong.Game;
import com.buttonHeck.pong.util.Options;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public abstract class ButtonController {

    private static boolean musicOn = true, soundsOn = true, itemsOn = true;
    private static ImageView startButton, exitButton, musicOnButton, soundsOnButton, itemsOnButton,
            easyButton, mediumButton, hardButton;
    private static ImageView classicLevel, magneticLevel, spaceLevel, returnBtn;
    private static ImageView[] items;

    static {
        GaussianBlur buttonEffect = new GaussianBlur(20);
        startButton = new ImageView(ImageController.getButtonImage());
        startButton.setEffect(buttonEffect);

        exitButton = new ImageView(ImageController.getButtonImage());
        exitButton.setEffect(buttonEffect);

        classicLevel = new ImageView(ImageController.getButtonImage());
        classicLevel.setEffect(buttonEffect);

        returnBtn = new ImageView(ImageController.getButtonImage());
        returnBtn.setEffect(buttonEffect);

        magneticLevel = new ImageView(ImageController.getButtonImage());
        magneticLevel.setEffect(buttonEffect);

        spaceLevel = new ImageView(ImageController.getButtonImage());
        spaceLevel.setEffect(buttonEffect);

        musicOnButton = new ImageView(ImageController.getSwitchImage(true));
        musicOnButton.setOnMouseClicked(e -> {
            setMusicOn();
            AudioController.buttonClicked();
        });
        ImageController.setSwitchStyle(musicOnButton, true);

        soundsOnButton = new ImageView(ImageController.getSwitchImage(true));
        soundsOnButton.setOnMouseClicked(e -> {
            setSoundsOn();
            AudioController.buttonClicked();
        });
        ImageController.setSwitchStyle(soundsOnButton, true);

        itemsOnButton = new ImageView(ImageController.getSwitchImage(true));
        itemsOnButton.setOnMouseClicked(e -> {
            setItemsOn();
            AudioController.buttonClicked();
        });
        ImageController.setSwitchStyle(itemsOnButton, true);

        easyButton = new ImageView(ImageController.getSwitchImage(false));
        ImageController.setSwitchStyle(easyButton, false);
        mediumButton = new ImageView(ImageController.getSwitchImage(true));
        ImageController.setSwitchStyle(mediumButton, true);
        hardButton = new ImageView(ImageController.getSwitchImage(false));
        ImageController.setSwitchStyle(hardButton, false);

        easyButton.setOnMouseClicked(e -> {
            ImageController.setDifficultyCheckboxImage(easyButton, mediumButton, hardButton);
            Options.setDifficulty(Options.Difficulty.EASY);
            AudioController.buttonClicked();
        });
        mediumButton.setOnMouseClicked(e -> {
            ImageController.setDifficultyCheckboxImage(mediumButton, easyButton, hardButton);
            Options.setDifficulty(Options.Difficulty.MEDIUM);
            AudioController.buttonClicked();
        });
        hardButton.setOnMouseClicked(e -> {
            ImageController.setDifficultyCheckboxImage(hardButton, mediumButton, easyButton);
            Options.setDifficulty(Options.Difficulty.HARD);
            AudioController.buttonClicked();
        });

        items = new ImageView[10];
        for (int i = 0; i < items.length; i++) {
            items[i] = new ImageView(ImageController.getMenuItemImage(i));
        }
        ImageController.setItemEffect(items[0], 1);
        ImageController.setItemEffect(items[1], -1);
        ImageController.setItemEffect(items[2], 0);
        ImageController.setItemEffect(items[3], 1);
        ImageController.setItemEffect(items[4], 0);
        ImageController.setItemEffect(items[5], 1);
        ImageController.setItemEffect(items[6], -1);
        ImageController.setItemEffect(items[7], 1);
        ImageController.setItemEffect(items[8], -1);
        ImageController.setItemEffect(items[9], 0);
    }

    public static void initStartButton(Text startText, Stage stage, Scene levelScene) {
        startButton.setOnMouseClicked(e -> {
            chooseLevel(startText, stage, levelScene);
            AudioController.buttonClicked();
        });
        startText.setOnMouseClicked(e -> {
            chooseLevel(startText, stage, levelScene);
            AudioController.buttonClicked();
        });
        startText.setOnMouseEntered(e -> buttonHovered(startButton, startText, true));
        startButton.setOnMouseEntered(e -> buttonHovered(startButton, startText, true));
        startButton.setOnMouseExited(e -> buttonHovered(startButton, startText, false));
    }

    private static void chooseLevel(Text startText, Stage stage, Scene levelScene) {
        buttonHovered(startButton, startText, false);
        stage.setScene(levelScene);
    }

    public static void initExitButton(Text exitText) {
        exitButton.setOnMouseClicked(e -> {
            AudioController.finish();
            System.exit(0);
        });
        exitText.setOnMouseClicked(e -> {
            AudioController.finish();
            System.exit(0);
        });
        exitButton.setOnMouseEntered(e -> buttonHovered(exitButton, exitText, true));
        exitText.setOnMouseEntered(e -> buttonHovered(exitButton, exitText, true));
        exitButton.setOnMouseExited(e -> buttonHovered(exitButton, exitText, false));
    }

    public static void initClassicLevel(Text levelText, Stage stage, Scene gameScene, Timeline gameTimeline, Timeline countdown) {
        classicLevel.setOnMouseClicked(e -> {
            startGame(classicLevel, levelText, stage, gameScene, gameTimeline, countdown);
            AudioController.buttonClicked();
        });
        levelText.setOnMouseClicked(e -> {
            startGame(classicLevel, levelText, stage, gameScene, gameTimeline, countdown);
            AudioController.buttonClicked();
        });
        levelText.setOnMouseEntered(e -> buttonHovered(classicLevel, levelText, true));
        classicLevel.setOnMouseEntered(e -> buttonHovered(classicLevel, levelText, true));
        classicLevel.setOnMouseExited(e -> buttonHovered(classicLevel, levelText, false));
    }

    public static void initMagneticLevel(Text levelText, Stage stage, Scene gameScene, Timeline gameTimeline, Timeline countdown) {
        magneticLevel.setOnMouseClicked(e -> {
            startGame(magneticLevel, levelText, stage, gameScene, gameTimeline, countdown);
            AudioController.buttonClicked();
        });
        levelText.setOnMouseClicked(e -> {
            startGame(magneticLevel, levelText, stage, gameScene, gameTimeline, countdown);
            AudioController.buttonClicked();
        });
        levelText.setOnMouseEntered(e -> buttonHovered(magneticLevel, levelText, true));
        magneticLevel.setOnMouseEntered(e -> buttonHovered(magneticLevel, levelText, true));
        magneticLevel.setOnMouseExited(e -> buttonHovered(magneticLevel, levelText, false));
    }

    public static void initSpaceLevel(Text levelText, Stage stage, Scene gameScene, Timeline gameTimeline, Timeline countdown) {
        spaceLevel.setOnMouseClicked(e -> {
            startGame(spaceLevel, levelText, stage, gameScene, gameTimeline, countdown);
            AudioController.buttonClicked();
        });
        levelText.setOnMouseClicked(e -> {
            startGame(spaceLevel, levelText, stage, gameScene, gameTimeline, countdown);
            AudioController.buttonClicked();
        });
        levelText.setOnMouseEntered(e -> buttonHovered(spaceLevel, levelText, true));
        spaceLevel.setOnMouseEntered(e -> buttonHovered(spaceLevel, levelText, true));
        spaceLevel.setOnMouseExited(e -> buttonHovered(spaceLevel, levelText, false));
    }

    private static void startGame(ImageView currentButton, Text startText, Stage stage, Scene gameScene, Timeline gameTimeline, Timeline countdown) {
        Game.resetScore();
        buttonHovered(currentButton, startText, false);
        if (currentButton == magneticLevel) {
            Game.setMagneticLevel();
        } else if (currentButton == classicLevel) {
            Game.setClassicLevel();
        } else if (currentButton == spaceLevel) {
            Game.setSpaceLevel();
        }
        stage.setScene(gameScene);
        countdown.play();
        countdown.setOnFinished(e -> {
            gameTimeline.play();
            Game.resetCountdown();
        });
    }

    public static void initReturnButton(Text buttonText) {
        returnBtn.setOnMouseClicked(e -> {
            returnToMenu(returnBtn, buttonText);
            AudioController.buttonClicked();
        });
        buttonText.setOnMouseClicked(e -> {
            returnToMenu(returnBtn, buttonText);
            AudioController.buttonClicked();
        });
        buttonText.setOnMouseEntered(e -> buttonHovered(returnBtn, buttonText, true));
        returnBtn.setOnMouseEntered(e -> buttonHovered(returnBtn, buttonText, true));
        returnBtn.setOnMouseExited(e -> buttonHovered(returnBtn, buttonText, false));
    }

    private static void returnToMenu(ImageView currentButton, Text buttonText) {
        buttonHovered(currentButton, buttonText, false);
        Game.returnToMenu();
    }

    private static void buttonHovered(ImageView button, Text text, boolean hovered) {
        ImageController.setButtonHoveredImage(button, hovered);
        TextController.setHovered(text, hovered);
    }

    //Getters
    public static ImageView getStartButton() {
        return startButton;
    }

    public static ImageView getExitButton() {
        return exitButton;
    }

    public static ImageView getMusicSwitch() {
        return musicOnButton;
    }

    public static ImageView getSoundsSwitch() {
        return soundsOnButton;
    }

    public static ImageView getItemsSwitch() {
        return itemsOnButton;
    }

    public static ImageView getEasyButton() {
        return easyButton;
    }

    public static ImageView getMediumButton() {
        return mediumButton;
    }

    public static ImageView getHardButton() {
        return hardButton;
    }

    public static ImageView getItem(int itemIndex) {
        return items[itemIndex];
    }

    public static ImageView getClassicLevelBtn() {
        return classicLevel;
    }

    public static ImageView getMagneticLevelBtn() {
        return magneticLevel;
    }

    public static ImageView getSpaceLevelBtn() {
        return spaceLevel;
    }

    public static ImageView getReturnBtn() {
        return returnBtn;
    }

    //Setters
    private static void setMusicOn() {
        musicOn = !musicOn;
        ImageController.setSwitchStyle(musicOnButton, musicOn);
        if (musicOn)
            AudioController.playMusic();
        else
            AudioController.stopMusic();
    }

    private static void setSoundsOn() {
        soundsOn = !soundsOn;
        AudioController.setSoundsOn(soundsOn);
        ImageController.setSwitchStyle(soundsOnButton, soundsOn);
    }

    private static void setItemsOn() {
        itemsOn = !itemsOn;
        ImageController.setSwitchStyle(itemsOnButton, itemsOn);
        Options.setItemsOn(itemsOn);
    }
}