package com.buttonHeck.pong.handler;

import com.buttonHeck.pong.Game;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class ImageHandler {

    private static final int SCALE = 4;
    private static final DropShadow ON_SHADOW = new DropShadow(24, Color.LAWNGREEN);
    private static final DropShadow OFF_SHADOW = new DropShadow(24, Color.RED);
    private static final DropShadow MENU_ITEM_FX = new DropShadow(4, Color.LIGHTBLUE);

    private static BufferedImage spriteSheet, classicSheet, magneticSheet, spaceSheet;
    private static BufferedImage ballSW;
    private static BufferedImage buttonSW, buttonHoveredSW;
    private static BufferedImage onSW, offSW;
    private static BufferedImage menuItemsSW[], itemsSW[];
    private static BufferedImage magnetSW, blackholeSW;

    private static Image classicFX, magneticFX, spaceFX;
    private static Image menuItemsFX[], itemsFX[];
    private static Image ballFX, buttonFX, buttonHoveredFX, onFX, offFX;
    private static Image magnetFX, blackholeFX;

    static {
        try {
            initializeSwingObjects();
            initializeFXObjects();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initializeSwingObjects() throws IOException {
        initializeSheets();
        initializeButtonImages();
        initializeSwitchImages();
        initializeMenuItems();
        initializeGameItems();
        ballSW = spriteSheet.getSubimage(0, 80, 20, 20);
        magnetSW = getScaledBufferedImage(spriteSheet.getSubimage(20, 80, 80, 10), 2);
        blackholeSW = spriteSheet.getSubimage(100, 80, 28, 28);
    }

    private static void initializeSheets() throws IOException {
        spriteSheet = ImageIO.read(Game.class.getResource("/images/spritesheet.png"));
        classicSheet = ImageIO.read(Game.class.getResource("/images/classic.png"));
        magneticSheet = ImageIO.read(Game.class.getResource("/images/magnetic.png"));
        spaceSheet = ImageIO.read(Game.class.getResource("/images/space.png"));
    }

    private static void initializeButtonImages() {
        buttonSW = getScaledBufferedImage(spriteSheet.getSubimage(0, 100, 40, 20), SCALE);
        buttonHoveredSW = spriteSheet.getSubimage(0, 100, 40, 20);
        for (int y = 0; y < buttonHoveredSW.getHeight(); y++)
            for (int x = 0; x < buttonHoveredSW.getWidth(); x++)
                buttonHoveredSW.setRGB(x, y, buttonHoveredSW.getRGB(x, y) + 0x006060bf);
        buttonHoveredSW = getScaledBufferedImage(buttonHoveredSW, SCALE);
    }

    private static void initializeSwitchImages() {
        initializeSwitchOnImages();
        initializeSwitchOffImages();
    }

    private static void initializeSwitchOnImages() {
        onSW = getScaledBufferedImage(spriteSheet.getSubimage(0, 80, 20, 20), 2);
        for (int y = 0; y < onSW.getHeight(); y++) {
            for (int x = 0; x < onSW.getWidth(); x++) {
                if (onSW.getRGB(x, y) == 0xFFdcdcdc)
                    onSW.setRGB(x, y, 0xFF38ff30);
                if (onSW.getRGB(x, y) == 0xFFb3b3b3)
                    onSW.setRGB(x, y, 0xFF2ec02c);
            }
        }
    }

    private static void initializeSwitchOffImages() {
        offSW = getScaledBufferedImage(spriteSheet.getSubimage(0, 80, 20, 20), 2);
        for (int y = 0; y < offSW.getHeight(); y++) {
            for (int x = 0; x < offSW.getWidth(); x++) {
                if (offSW.getRGB(x, y) == 0xFFdcdcdc)
                    offSW.setRGB(x, y, 0xFFdc0000);
                if (offSW.getRGB(x, y) == 0xFFb3b3b3)
                    offSW.setRGB(x, y, 0xFFaa0000);
            }
        }
    }

    private static void initializeMenuItems() {
        menuItemsSW = new BufferedImage[]{
                getScaledBufferedImage(spriteSheet.getSubimage(0, 0, 40, 40), 2),
                getScaledBufferedImage(spriteSheet.getSubimage(40, 0, 40, 40), 2),
                getScaledBufferedImage(spriteSheet.getSubimage(80, 0, 40, 40), 2),
                getScaledBufferedImage(spriteSheet.getSubimage(120, 0, 40, 40), 2),
                getScaledBufferedImage(spriteSheet.getSubimage(40, 40, 40, 40), 2),
                getScaledBufferedImage(spriteSheet.getSubimage(160, 0, 40, 40), 2),
                getScaledBufferedImage(spriteSheet.getSubimage(0, 40, 40, 40), 2),
                getScaledBufferedImage(spriteSheet.getSubimage(80, 40, 40, 40), 2),
                getScaledBufferedImage(spriteSheet.getSubimage(120, 40, 40, 40), 2),
                getScaledBufferedImage(spriteSheet.getSubimage(160, 40, 40, 40), 2)
        };
    }

    private static void initializeGameItems() {
        itemsSW = new BufferedImage[]{
                spriteSheet.getSubimage(0, 0, 40, 40),
                spriteSheet.getSubimage(40, 0, 40, 40),
                spriteSheet.getSubimage(80, 0, 40, 40),
                spriteSheet.getSubimage(120, 0, 40, 40),
                spriteSheet.getSubimage(40, 40, 40, 40),
                spriteSheet.getSubimage(160, 0, 40, 40),
                spriteSheet.getSubimage(0, 40, 40, 40),
                spriteSheet.getSubimage(80, 40, 40, 40),
                spriteSheet.getSubimage(120, 40, 40, 40),
                spriteSheet.getSubimage(160, 40, 40, 40),
        };
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
        for (int i = 0; i < menuItemsFX.length; i++)
            menuItemsFX[i] = SwingFXUtils.toFXImage(menuItemsSW[i], null);
        itemsFX = new Image[10];
        for (int i = 0; i < itemsFX.length; i++)
            itemsFX[i] = SwingFXUtils.toFXImage(itemsSW[i], null);
        magnetFX = SwingFXUtils.toFXImage(magnetSW, null);
        blackholeFX = SwingFXUtils.toFXImage(blackholeSW, null);
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

    //Getters

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

    public static Image getClassicSheet() {
        return classicFX;
    }

    public static Image getMagneticSheet() {
        return magneticFX;
    }

    public static Image getSpaceSheet() {
        return spaceFX;
    }

    //Setters

    static void setButtonHoveredImage(ImageView button, boolean hovered) {
        button.setImage(hovered ? buttonHoveredFX : buttonFX);
    }

    static void setSwitchStyle(ImageView button, boolean on) {
        button.setImage(on ? onFX : offFX);
        button.setEffect(on ? ON_SHADOW : OFF_SHADOW);
    }

    static void setItemEffect(ImageView item) {
        item.setEffect(MENU_ITEM_FX);
    }

    static void setDifficultyCheckboxImage(ImageView activeButton, ImageView nonActive1, ImageView nonActive2) {
        setSwitchStyle(activeButton, true);
        setSwitchStyle(nonActive1, false);
        setSwitchStyle(nonActive2, false);
    }
}
