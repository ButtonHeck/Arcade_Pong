package com.buttonHeck.pong.controllers;

import com.buttonHeck.pong.Game;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class ImageController {

    private static final int SCALE = 4;
    private static BufferedImage classicSheet;
    private static BufferedImage magneticSheet;
    private static BufferedImage spaceSheet;
    private static BufferedImage ballSW;
    private static BufferedImage buttonSW;
    private static BufferedImage buttonHoveredSW;
    private static BufferedImage onSW;
    private static BufferedImage offSW;
    private static BufferedImage menuItemsSW[];
    private static BufferedImage itemsSW[];
    private static BufferedImage magnetSW, blackholeSW;

    private static Image classicFX, magneticFX, spaceFX;
    private static Image menuItemsFX[];
    private static Image itemsFX[];
    private static Image ballFX, buttonFX, buttonHoveredFX, onFX, offFX;
    private static Image magnetFX, blackholeFX;

    private static DropShadow onShadow = new DropShadow(24, Color.LAWNGREEN);
    private static DropShadow offShadow = new DropShadow(24, Color.RED);
    private static DropShadow positiveBonusEffect = new DropShadow(24, Color.LAWNGREEN);
    private static DropShadow negativeBonusEffect = new DropShadow(24, Color.RED);
    private static DropShadow neutralBonusEffect = new DropShadow(24, Color.WHITE);

    static {
        try {
            initializeSwingObjects();
            initializeFXObjects();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initializeSwingObjects() throws IOException {
        BufferedImage sheet = ImageIO.read(Game.class.getResource("/images/spritesheet.png"));
        classicSheet = ImageIO.read(Game.class.getResource("/images/classic.png"));
        magneticSheet = ImageIO.read(Game.class.getResource("/images/magnetic.png"));
        spaceSheet = ImageIO.read(Game.class.getResource("/images/space.png"));
        ballSW = sheet.getSubimage(0, 80, 20, 20);
        buttonSW = getScaledBufferedImage(sheet.getSubimage(0, 100, 40, 20), SCALE);
        buttonHoveredSW = sheet.getSubimage(0, 100, 40, 20);
        for (int y = 0; y < buttonHoveredSW.getHeight(); y++) {
            for (int x = 0; x < buttonHoveredSW.getWidth(); x++) {
                buttonHoveredSW.setRGB(x, y, buttonHoveredSW.getRGB(x, y) + 0x006060bf);
            }
        }
        buttonHoveredSW = getScaledBufferedImage(buttonHoveredSW, SCALE);

        onSW = getScaledBufferedImage(sheet.getSubimage(0, 80, 20, 20), 2);
        offSW = getScaledBufferedImage(sheet.getSubimage(0, 80, 20, 20), 2);
        for (int y = 0; y < onSW.getHeight(); y++) {
            for (int x = 0; x < onSW.getWidth(); x++) {
                if (onSW.getRGB(x, y) == 0xFFdcdcdc)
                    onSW.setRGB(x, y, 0xFF38ff30);
                if (onSW.getRGB(x, y) == 0xFFb3b3b3)
                    onSW.setRGB(x, y, 0xFF2ec02c);
            }
        }
        for (int y = 0; y < offSW.getHeight(); y++) {
            for (int x = 0; x < offSW.getWidth(); x++) {
                if (offSW.getRGB(x, y) == 0xFFdcdcdc)
                    offSW.setRGB(x, y, 0xFFdc0000);
                if (offSW.getRGB(x, y) == 0xFFb3b3b3)
                    offSW.setRGB(x, y, 0xFFaa0000);
            }
        }
        menuItemsSW = new BufferedImage[]{
                getScaledBufferedImage(sheet.getSubimage(0, 0, 40, 40), 2),
                getScaledBufferedImage(sheet.getSubimage(40, 0, 40, 40), 2),
                getScaledBufferedImage(sheet.getSubimage(80, 0, 40, 40), 2),
                getScaledBufferedImage(sheet.getSubimage(120, 0, 40, 40), 2),
                getScaledBufferedImage(sheet.getSubimage(40, 40, 40, 40), 2),
                getScaledBufferedImage(sheet.getSubimage(160, 0, 40, 40), 2),
                getScaledBufferedImage(sheet.getSubimage(0, 40, 40, 40), 2),
                getScaledBufferedImage(sheet.getSubimage(80, 40, 40, 40), 2),
                getScaledBufferedImage(sheet.getSubimage(120, 40, 40, 40), 2),
                getScaledBufferedImage(sheet.getSubimage(160, 40, 40, 40), 2)
        };
        itemsSW = new BufferedImage[]{
                sheet.getSubimage(0, 0, 40, 40),
                sheet.getSubimage(40, 0, 40, 40),
                sheet.getSubimage(80, 0, 40, 40),
                sheet.getSubimage(120, 0, 40, 40),
                sheet.getSubimage(40, 40, 40, 40),
                sheet.getSubimage(160, 0, 40, 40),
                sheet.getSubimage(0, 40, 40, 40),
                sheet.getSubimage(80, 40, 40, 40),
                sheet.getSubimage(120, 40, 40, 40),
                sheet.getSubimage(160, 40, 40, 40),
        };
        magnetSW = getScaledBufferedImage(sheet.getSubimage(20, 80, 80, 10), 2);
        blackholeSW = sheet.getSubimage(100, 80, 28, 28);
    }

    private static void initializeFXObjects() {
        classicFX = SwingFXUtils.toFXImage(classicSheet, null);
        magneticFX = SwingFXUtils.toFXImage(magneticSheet, null);
        spaceFX = SwingFXUtils.toFXImage(spaceSheet, null);
        ballFX = SwingFXUtils.toFXImage(ballSW, null);
        buttonFX = SwingFXUtils.toFXImage(buttonSW, null);
        buttonHoveredFX = SwingFXUtils.toFXImage(buttonHoveredSW, null);
        onFX = SwingFXUtils.toFXImage(onSW, null);
        offFX = SwingFXUtils.toFXImage(offSW, null);

        menuItemsFX = new Image[10];
        for (int i = 0; i < menuItemsFX.length; i++) {
            menuItemsFX[i] = SwingFXUtils.toFXImage(menuItemsSW[i], null);
        }
        itemsFX = new Image[10];
        for (int i = 0; i < itemsFX.length; i++) {
            itemsFX[i] = SwingFXUtils.toFXImage(itemsSW[i], null);
        }
        magnetFX = SwingFXUtils.toFXImage(magnetSW, null);
        blackholeFX = SwingFXUtils.toFXImage(blackholeSW, null);
    }

    static void setButtonHoveredImage(ImageView button, boolean hovered) {
        button.setImage(hovered ? buttonHoveredFX : buttonFX);
    }

    public static Image getBallImage() {
        return ballFX;
    }

    static Image getButtonImage() {
        return buttonFX;
    }

    public static Image getSwitchImage(boolean on) {
        return on ? onFX : offFX;
    }

    static Image getMenuItemImage(int i) {
        return menuItemsFX[i];
    }

    public static Image getItemImage(int i) {
        return itemsFX[i];
    }

    public static Image getMagnetImage() {
        return magnetFX;
    }

    public static Image getBlackholeImage() {
        return blackholeFX;
    }

    static void setSwitchStyle(ImageView button, boolean on) {
        button.setImage(on ? onFX : offFX);
        button.setEffect(on ? onShadow : offShadow);
    }

    static void setItemEffect(ImageView item, int effect) {
        switch (effect) {
            case 1:
                item.setEffect(positiveBonusEffect);
                break;
            case -1:
                item.setEffect(negativeBonusEffect);
                break;
            case 0:
                item.setEffect(neutralBonusEffect);
        }
    }

    static void setDifficultyCheckboxImage(ImageView activeButton, ImageView nonActive1, ImageView nonActive2) {
        setSwitchStyle(activeButton, true);
        setSwitchStyle(nonActive1, false);
        setSwitchStyle(nonActive2, false);
    }

    private static BufferedImage getScaledBufferedImage(BufferedImage original, int scale) {
        int newWidth = original.getWidth() * scale;
        int newHeight = original.getHeight() * scale;
        BufferedImage result = new BufferedImage(newWidth, newHeight, original.getType());
        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int color = original.getRGB(x, y);
                for (int yOffset = 0; yOffset < scale; yOffset++) {
                    for (int xOffset = 0; xOffset < scale; xOffset++) {
                        result.setRGB(x * scale + xOffset, y * scale + yOffset, color);
                    }
                }
            }
        }
        return result;
    }

    public static Image getClassicSheet() {
        return classicFX;
    }

    public static Image getMagneticSheet() {
        return magneticFX;
    }

    public static Image getSpaceSheet() {
        return spaceFX;
    }
}
