package com.buttonHeck.pong.handler;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.lwjgl.openal.AL;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public abstract class AudioHandler {

    private static Sound batReflection, blackhole, buttonClicked, screenReflection,
            itemPicked, pistolPicked, shot, timeStopper, result, countdown;
    private static boolean soundsEnabled = true;
    private static MediaPlayer music;

    static {
        try {
            batReflection = new Sound(AudioHandler.class.getResource("/audio/batReflection.ogg"));
            blackhole = new Sound(AudioHandler.class.getResource("/audio/blackhole.ogg"));
            buttonClicked = new Sound(AudioHandler.class.getResource("/audio/buttonClicked.ogg"));
            itemPicked = new Sound(AudioHandler.class.getResource("/audio/itemPicked.ogg"));
            pistolPicked = new Sound(AudioHandler.class.getResource("/audio/pistolPicked.ogg"));
            timeStopper = new Sound(AudioHandler.class.getResource("/audio/timeStopper.ogg"));
            result = new Sound(AudioHandler.class.getResource("/audio/result.ogg"));
            screenReflection = new Sound(AudioHandler.class.getResource("/audio/screenReflection.ogg"));
            countdown = new Sound(AudioHandler.class.getResource("/audio/countdown.ogg"));
            shot = new Sound(AudioHandler.class.getResource("/audio/shot.ogg"));
            initializeMusic();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    private static void initializeMusic() {
        Media musicFile = new Media(AudioHandler.class.getResource("/audio/music.mp3").toExternalForm());
        music = new MediaPlayer(musicFile);
        music.setCycleCount(MediaPlayer.INDEFINITE);
        music.setVolume(0.4);
    }

    public static void playMusic() {
        music.play();
    }

    static void stopMusic() {
        music.stop();
    }

    public static void batReflection() {
        if (soundsEnabled)
            batReflection.play((float) (Math.random() * 0.2 + 0.8), 1.0f);
    }

    public static void blackhole() {
        if (soundsEnabled)
            blackhole.play();
    }

    public static void buttonClicked() {
        if (soundsEnabled)
            buttonClicked.play((float) (Math.random() * 0.1 + 0.9), 1.0f);
    }

    public static void itemPicked() {
        if (soundsEnabled)
            itemPicked.play(1.0f, 0.75f);
    }

    public static void pistolPicked() {
        if (soundsEnabled)
            pistolPicked.play();
    }

    public static void timeStopper() {
        if (soundsEnabled)
            timeStopper.play();
    }

    public static void result() {
        if (soundsEnabled)
            result.play(1.0f, 0.6f);
    }

    public static void screenReflection() {
        if (soundsEnabled)
            screenReflection.play((float) (Math.random() * 0.2 + 0.8), 0.9f);
    }

    public static void shot() {
        if (soundsEnabled)
            shot.play();
    }

    public static void countdown() {
        if (soundsEnabled)
            countdown.play();
    }

    public static void finish() {
        stopMusic();
        AL.destroy();
    }

    static void setSoundsOn(boolean isOn) {
        soundsEnabled = isOn;
    }
}
