package com.buttonHeck.pong.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.buttonHeck.pong.Game.HEIGHT;
import static com.buttonHeck.pong.Game.WIDTH;

public abstract class TextController {

    private final static Font FONT = Font.font("Dialog", 28);
    private final static Font COUNTDOWN_FONT = new Font("Arial", 96 * 4);
    private final static Font RESULT_FONT = new Font("Arial", 96);
    private static Light.Point lightPoint = new Light.Point(384, 96, 80, Color.WHITE);
    private static final Lighting LIGHTING = new Lighting(new Light.Point(96, 96, 80, Color.WHITE));
    private static final Lighting MESSAGE_LIGHTING = new Lighting(lightPoint);
    private static final boolean DIFFUSE_DIRECTION_UP[] = new boolean[]{false};
    private final static GaussianBlur TEXT_EFFECT = new GaussianBlur(1);
    private static Timeline lightingAnimation = new Timeline();

    private final static Text itemsTexts[] = new Text[]{
            new Text("Increase bat size"),
            new Text("Decrease bat size"),
            new Text("Stop time (2s, press E)"),
            new Text("+1 Score"),
            new Text("Swap bats"),
            new Text("Increase bat speed"),
            new Text("Decrease bat speed"),
            new Text("Increase ball speed"),
            new Text("Decrease ball speed"),
            new Text("x3 Pistol (Press SPACE)")};
    private static final Text texts[] = new Text[]{
            new Text("START!"),
            new Text("EXIT"),
            new Text("Music"),
            new Text("Sounds"),
            new Text("Items"),
            new Text("Difficulty:"),
            new Text("Easy"),
            new Text("Medium"),
            new Text("Hard"),};
    private static final Text levelTexts[] = new Text[]{
            new Text("Classic"),
            new Text("Magnetic"),
            new Text("Space"),
            new Text("Return"),
            new Text("W - Up, S - Down, SHIFT+ W/S - precise move"),
            new Text("E - activate time stop for 2 seconds"),
            new Text("SPACE - shoot, stunning opponent for 0.5 sec if hit")
    };
    private static Text countdown = new Text(WIDTH / 2 - 130, HEIGHT / 2 + 118, "3");
    private static Text youWin = new Text(150, HEIGHT / 2 - 48, "WIN! Press ESC");
    private static Text youLose = new Text(100, HEIGHT / 2 - 48, "LOSE! Press ESC");
    private static Text timeStopDurationText = new Text(String.valueOf(2.0));
    private static Text authorText = new Text(WIDTH / 1.35, HEIGHT - 10, "created by ButtonHeck, 2017");

    static {
        applySettings(texts);
        applySettings(itemsTexts);
        applySettings(levelTexts);
        authorText.setFill(Color.WHITE);
        authorText.setFont(new Font("Arial", 16));
        countdown.setFill(Color.WHITE);
        countdown.setFont(COUNTDOWN_FONT);
        countdown.setEffect(LIGHTING);
        MESSAGE_LIGHTING.setDiffuseConstant(1.9);
        youWin.setFont(RESULT_FONT);
        youWin.setFill(Color.LAWNGREEN);
        youWin.setEffect(MESSAGE_LIGHTING);
        youLose.setFont(RESULT_FONT);
        youLose.setFill(Color.ORANGERED);
        youLose.setEffect(MESSAGE_LIGHTING);
        timeStopDurationText.setFill(Color.WHITE);
        timeStopDurationText.setFont(new Font("Monospaced", 24));

        KeyFrame lightingFrame = new KeyFrame(Duration.millis(10), event -> {
            lightPoint.setX(lightPoint.getX() + (DIFFUSE_DIRECTION_UP[0] ? 3 : -3));
            if (lightPoint.getX() >= 700 || lightPoint.getX() <= 100)
                DIFFUSE_DIRECTION_UP[0] = !DIFFUSE_DIRECTION_UP[0];
        });
        lightingAnimation.getKeyFrames().add(lightingFrame);
        lightingAnimation.setCycleCount(Animation.INDEFINITE);
    }

    private static void applySettings(Text[] texts) {
        for (Text text : texts) {
            text.setFont(FONT);
            text.setFill(Color.WHITE);
            text.setEffect(TEXT_EFFECT);
        }
    }

    public static Text[] getItemsTexts() {
        return itemsTexts;
    }

    public static Text[] getTexts() {
        return texts;
    }

    public static Text[] getLevelTexts() {
        return levelTexts;
    }

    static void setHovered(Text text, boolean hovered) {
        text.setFill(hovered ? Color.BLACK : Color.WHITE);
    }

    public static Text getCountdown() {
        return countdown;
    }

    public static Text getResultMessage(boolean win) {
        return win ? youWin : youLose;
    }

    public static Timeline getResultAnimation() {
        return lightingAnimation;
    }

    public static Text getTimeStopDurationText() {
        return timeStopDurationText;
    }

    public static Text getAuthorText() {
        return authorText;
    }
}
