package com.buttonHeck.pong.controllers;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.openal.WaveData;

public abstract class AudioController {

    private static Sound batReflection, blackhole, buttonClicked, screenReflection,
            itemPicked, pistolPicked, shot, timeStopper, result, countdown;
    private static boolean soundsEnabled = true;
    private static int music;

    static {
        try {
            batReflection = new Sound(AudioController.class.getResource("/audio/batReflection.ogg"));
            blackhole = new Sound(AudioController.class.getResource("/audio/blackhole.ogg"));
            buttonClicked = new Sound(AudioController.class.getResource("/audio/buttonClicked.ogg"));
            itemPicked = new Sound(AudioController.class.getResource("/audio/itemPicked.ogg"));
            pistolPicked = new Sound(AudioController.class.getResource("/audio/pistolPicked.ogg"));
            timeStopper = new Sound(AudioController.class.getResource("/audio/timeStopper.ogg"));
            result = new Sound(AudioController.class.getResource("/audio/result.ogg"));
            screenReflection = new Sound(AudioController.class.getResource("/audio/screenReflection.ogg"));
            countdown = new Sound(AudioController.class.getResource("/audio/countdown.ogg"));
            shot = new Sound(AudioController.class.getResource("/audio/shot.ogg"));
        } catch (SlickException e) {
            e.printStackTrace();
        }
        WaveData musicData = WaveData.create(AudioController.class.getResource("/audio/music.wav"));
        int musicBuffer = AL10.alGenBuffers();
        AL10.alBufferData(musicBuffer, musicData.format, musicData.data, musicData.samplerate);
        musicData.dispose();
        music = AL10.alGenSources();
        AL10.alSourcei(music, AL10.AL_BUFFER, musicBuffer);
    }

    public static void playMusic() {
        AL10.alSourcef(music, AL10.AL_GAIN, 0.4f);
        AL10.alSourcei(music, AL10.AL_LOOPING, 1);
        AL10.alSourcePlay(music);
    }

    static void stopMusic() {
        AL10.alSourceStop(music);
    }

    public static void batReflection() {
        if (soundsEnabled) {
            batReflection.play((float) (Math.random() * 0.2 + 0.8), 1.0f);
        }
    }

    public static void blackhole() {
        if (soundsEnabled)
            blackhole.play();
    }

    static void buttonClicked() {
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
