package com.buttonHeck.pong.handler;

import com.buttonHeck.pong.Game;
import com.buttonHeck.pong.util.Options;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public abstract class ButtonHandler {

    private static ImageView startButton, exitButton, musicOnButton, soundsOnButton, itemsOnButton,
            easyButton, mediumButton, hardButton;
    private static ImageView classicLevel, magneticLevel, spaceLevel, returnBtn;
    private static ImageView[] items;

    static {
        initializeButtons();
        initializeSwitches();
        initializeDifficultySwitches();
        initializeMenuItems();
    }

    private static void initializeButtons() {
        GaussianBlur buttonEffect = new GaussianBlur(20);
        startButton = new ImageView(ImageHandler.getButtonImage());
        startButton.setEffect(buttonEffect);
        exitButton = new ImageView(ImageHandler.getButtonImage());
        exitButton.setEffect(buttonEffect);
        classicLevel = new ImageView(ImageHandler.getButtonImage());
        classicLevel.setEffect(buttonEffect);
        returnBtn = new ImageView(ImageHandler.getButtonImage());
        returnBtn.setEffect(buttonEffect);
        magneticLevel = new ImageView(ImageHandler.getButtonImage());
        magneticLevel.setEffect(buttonEffect);
        spaceLevel = new ImageView(ImageHandler.getButtonImage());
        spaceLevel.setEffect(buttonEffect);
    }

    private static void initializeSwitches() {
        musicOnButton = new ImageView(ImageHandler.getSwitchImage(true));
        musicOnButton.setOnMouseClicked(e -> clickMusicSwitch());
        ImageHandler.setSwitchStyle(musicOnButton, true);

        soundsOnButton = new ImageView(ImageHandler.getSwitchImage(true));
        soundsOnButton.setOnMouseClicked(e -> clickSoundsSwitch());
        ImageHandler.setSwitchStyle(soundsOnButton, true);

        itemsOnButton = new ImageView(ImageHandler.getSwitchImage(true));
        itemsOnButton.setOnMouseClicked(e -> clickItemsSwitch());
        ImageHandler.setSwitchStyle(itemsOnButton, true);
    }

    private static void initializeDifficultySwitches() {
        easyButton = new ImageView(ImageHandler.getSwitchImage(false));
        ImageHandler.setSwitchStyle(easyButton, false);
        mediumButton = new ImageView(ImageHandler.getSwitchImage(true));
        ImageHandler.setSwitchStyle(mediumButton, true);
        hardButton = new ImageView(ImageHandler.getSwitchImage(false));
        ImageHandler.setSwitchStyle(hardButton, false);

        easyButton.setOnMouseClicked(e -> {
            ImageHandler.setDifficultyCheckboxImage(easyButton, mediumButton, hardButton);
            Options.clickOnDifficultyCheckbox(Options.Difficulty.EASY);
        });
        mediumButton.setOnMouseClicked(e -> {
            ImageHandler.setDifficultyCheckboxImage(mediumButton, easyButton, hardButton);
            Options.clickOnDifficultyCheckbox(Options.Difficulty.MEDIUM);
        });
        hardButton.setOnMouseClicked(e -> {
            ImageHandler.setDifficultyCheckboxImage(hardButton, mediumButton, easyButton);
            Options.clickOnDifficultyCheckbox(Options.Difficulty.HARD);
        });
    }

    private static void initializeMenuItems() {
        items = new ImageView[10];
        for (int i = 0; i < items.length; i++) {
            items[i] = new ImageView(ImageHandler.getMenuItemImage(i));
            ImageHandler.setItemEffect(items[i]);
        }
    }

    public static void initStartButton(Text startText, Stage stage, Scene levelScene) {
        startButton.setOnMouseClicked(e -> clickChooseLevel(startText, stage, levelScene));
        startText.setOnMouseClicked(e -> clickChooseLevel(startText, stage, levelScene));
        startText.setOnMouseEntered(e -> buttonHovered(startButton, startText, true));
        startButton.setOnMouseEntered(e -> buttonHovered(startButton, startText, true));
        startButton.setOnMouseExited(e -> buttonHovered(startButton, startText, false));
    }

    private static void clickChooseLevel(Text startText, Stage stage, Scene levelScene) {
        AudioHandler.buttonClicked();
        buttonHovered(startButton, startText, false);
        stage.setScene(levelScene);
    }

    public static void initExitButton(Text exitText) {
        exitButton.setOnMouseClicked(e -> {
            AudioHandler.finish();
            System.exit(0);
        });
        exitText.setOnMouseClicked(e -> {
            AudioHandler.finish();
            System.exit(0);
        });
        exitButton.setOnMouseEntered(e -> buttonHovered(exitButton, exitText, true));
        exitText.setOnMouseEntered(e -> buttonHovered(exitButton, exitText, true));
        exitButton.setOnMouseExited(e -> buttonHovered(exitButton, exitText, false));
    }

    public static void initReturnButton(Text buttonText) {
        returnBtn.setOnMouseClicked(e -> clickReturnToMenu(returnBtn, buttonText));
        buttonText.setOnMouseClicked(e -> clickReturnToMenu(returnBtn, buttonText));
        buttonText.setOnMouseEntered(e -> buttonHovered(returnBtn, buttonText, true));
        returnBtn.setOnMouseEntered(e -> buttonHovered(returnBtn, buttonText, true));
        returnBtn.setOnMouseExited(e -> buttonHovered(returnBtn, buttonText, false));
    }

    public static void initClassicLevel(Text text, Stage stage, Scene scene, Timeline timeline, Timeline countdown) {
        classicLevel.setOnMouseClicked(e -> clickStartGame(classicLevel, text, stage, scene, timeline, countdown));
        text.setOnMouseClicked(e -> clickStartGame(classicLevel, text, stage, scene, timeline, countdown));
        text.setOnMouseEntered(e -> buttonHovered(classicLevel, text, true));
        classicLevel.setOnMouseEntered(e -> buttonHovered(classicLevel, text, true));
        classicLevel.setOnMouseExited(e -> buttonHovered(classicLevel, text, false));
    }

    public static void initMagneticLevel(Text text, Stage stage, Scene scene, Timeline timeline, Timeline countdown) {
        magneticLevel.setOnMouseClicked(e -> clickStartGame(magneticLevel, text, stage, scene, timeline, countdown));
        text.setOnMouseClicked(e -> clickStartGame(magneticLevel, text, stage, scene, timeline, countdown));
        text.setOnMouseEntered(e -> buttonHovered(magneticLevel, text, true));
        magneticLevel.setOnMouseEntered(e -> buttonHovered(magneticLevel, text, true));
        magneticLevel.setOnMouseExited(e -> buttonHovered(magneticLevel, text, false));
    }

    public static void initSpaceLevel(Text text, Stage stage, Scene scene, Timeline timeline, Timeline countdown) {
        spaceLevel.setOnMouseClicked(e -> clickStartGame(spaceLevel, text, stage, scene, timeline, countdown));
        text.setOnMouseClicked(e -> clickStartGame(spaceLevel, text, stage, scene, timeline, countdown));
        text.setOnMouseEntered(e -> buttonHovered(spaceLevel, text, true));
        spaceLevel.setOnMouseEntered(e -> buttonHovered(spaceLevel, text, true));
        spaceLevel.setOnMouseExited(e -> buttonHovered(spaceLevel, text, false));
    }

    private static void clickStartGame(ImageView button, Text text, Stage stage, Scene scene, Timeline timeline, Timeline countdown) {
        AudioHandler.buttonClicked();
        Game.globalReset();
        buttonHovered(button, text, false);
        if (button == magneticLevel)
            Game.setMagneticLevel();
        else if (button == classicLevel)
            Game.setClassicLevel();
        else if (button == spaceLevel)
            Game.setSpaceLevel();
        stage.setScene(scene);
        countdown.play();
        countdown.setOnFinished(e -> {
            timeline.play();
            Game.resetCountdown();
        });
    }

    private static void clickReturnToMenu(ImageView button, Text text) {
        AudioHandler.buttonClicked();
        buttonHovered(button, text, false);
        Game.returnToMenu();
    }

    private static void buttonHovered(ImageView button, Text text, boolean hovered) {
        ImageHandler.setButtonHoveredImage(button, hovered);
        TextHandler.setHovered(text, hovered);
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
    private static void clickMusicSwitch() {
        Options.setMusicOn(!Options.isMusicOn());
        ImageHandler.setSwitchStyle(musicOnButton, Options.isMusicOn());
        if (Options.isMusicOn())
            AudioHandler.playMusic();
        else
            AudioHandler.stopMusic();
        AudioHandler.buttonClicked();
    }

    private static void clickSoundsSwitch() {
        Options.setSoundsOn(!Options.isSoundsOn());
        AudioHandler.setSoundsOn(Options.isSoundsOn());
        ImageHandler.setSwitchStyle(soundsOnButton, Options.isSoundsOn());
        AudioHandler.buttonClicked();
    }

    private static void clickItemsSwitch() {
        Options.setItemsOn(!Options.isItemsOn());
        ImageHandler.setSwitchStyle(itemsOnButton, Options.isItemsOn());
        AudioHandler.buttonClicked();
    }
}