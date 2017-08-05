package com.buttonHeck.pong.handler;

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

public abstract class TextHandler {

    private final static Font FONT = Font.font("Dialog", 28);
    private final static Font COUNTDOWN_FONT = new Font("Arial", 96 * 4);
    private final static Font RESULT_FONT = new Font("Arial", 96);
    private final static Light.Point LIGHT_POINT = new Light.Point(384, 96, 80, Color.WHITE);
    private final static Lighting LIGHTING = new Lighting(new Light.Point(96, 96, 80, Color.WHITE));
    private final static Lighting MESSAGE_LIGHTING = new Lighting(LIGHT_POINT);
    private final static boolean DIFFUSE_DIRECTION_UP[] = new boolean[]{false};
    private final static GaussianBlur TEXT_EFFECT = new GaussianBlur(1);
    private static Timeline resultLightAnimation = new Timeline();

    private final static Text ITEMS_TEXTS[] = new Text[]{
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
    private static final Text BUTTONS_TEXTS[] = new Text[]{
            new Text("START!"),
            new Text("EXIT"),
            new Text("Music"),
            new Text("Sounds"),
            new Text("Items"),
            new Text("Difficulty:"),
            new Text("Easy"),
            new Text("Medium"),
            new Text("Hard"),};
    private static final Text LEVEL_TEXTS[] = new Text[]{
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
        applySettings(BUTTONS_TEXTS);
        applySettings(ITEMS_TEXTS);
        applySettings(LEVEL_TEXTS);
        initializeAuthorText();
        initializeCountdownText();
        MESSAGE_LIGHTING.setDiffuseConstant(1.9);
        initializeResultText(youWin, Color.LAWNGREEN);
        initializeResultText(youLose, Color.ORANGERED);
        initializeResultLightingAnimation();
        initializeTimeStopDurationText();
    }

    private static void applySettings(Text[] texts) {
        for (Text text : texts) {
            text.setFont(FONT);
            text.setFill(Color.WHITE);
            text.setEffect(TEXT_EFFECT);
        }
    }

    private static void initializeAuthorText() {
        authorText.setFill(Color.WHITE);
        authorText.setFont(new Font("Arial", 16));
    }

    private static void initializeCountdownText() {
        countdown.setFill(Color.WHITE);
        countdown.setFont(COUNTDOWN_FONT);
        countdown.setEffect(LIGHTING);
    }

    private static void initializeResultText(Text resultText, Color messageColor) {
        resultText.setFont(RESULT_FONT);
        resultText.setFill(messageColor);
        resultText.setEffect(MESSAGE_LIGHTING);
    }

    private static void initializeTimeStopDurationText() {
        timeStopDurationText.setFill(Color.WHITE);
        timeStopDurationText.setFont(new Font("Monospaced", 24));
    }

    private static void initializeResultLightingAnimation() {
        KeyFrame lightingFrame = new KeyFrame(Duration.millis(10), event -> {
            LIGHT_POINT.setX(LIGHT_POINT.getX() + (DIFFUSE_DIRECTION_UP[0] ? 3 : -3));
            if (LIGHT_POINT.getX() >= 700 || LIGHT_POINT.getX() <= 100)
                DIFFUSE_DIRECTION_UP[0] = !DIFFUSE_DIRECTION_UP[0];
        });
        resultLightAnimation.getKeyFrames().add(lightingFrame);
        resultLightAnimation.setCycleCount(Animation.INDEFINITE);
    }

    //Getters and Setters

    public static Text[] getItemsTexts() {
        return ITEMS_TEXTS;
    }

    public static Text[] getButtonsTexts() {
        return BUTTONS_TEXTS;
    }

    public static Text[] getLevelTexts() {
        return LEVEL_TEXTS;
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

    public static Timeline getResultLightAnimation() {
        return resultLightAnimation;
    }

    public static Text getTimeStopDurationText() {
        return timeStopDurationText;
    }

    public static Text getAuthorText() {
        return authorText;
    }
}
