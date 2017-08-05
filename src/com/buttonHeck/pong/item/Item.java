package com.buttonHeck.pong.items;

import com.buttonHeck.pong.handler.ImageHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Item extends ImageView {

    public final int ordinal;

    public Item(Image image, int ordinal) {
        super(image);
        this.ordinal = ordinal;
    }

    public abstract Item copy();
    public abstract void applyAction();

    public static Item getItemByOrdinal(int ordinal) {
        switch (ordinal) {
            case 0:
                return new BatSizeIncrease(ImageHandler.getItemImage(0), 0);
            case 1:
                return new BatSizeDecrease(ImageHandler.getItemImage(1), 1);
            case 2:
                return new TimeStopper(ImageHandler.getItemImage(2), 2);
            case 3:
                return new PlusScore(ImageHandler.getItemImage(3), 3);
            case 4:
                return new SwapBats(ImageHandler.getItemImage(4), 4);
            case 5:
                return new BatSpeedIncrease(ImageHandler.getItemImage(5), 5);
            case 6:
                return new BatSpeedDecrease(ImageHandler.getItemImage(6), 6);
            case 7:
                return new BallSpeedUp(ImageHandler.getItemImage(7), 7);
            case 8:
                return new BallSpeedDown(ImageHandler.getItemImage(8), 8);
            case 9:
                return new Pistol(ImageHandler.getItemImage(9), 9);
        }
        return null;
    }
}
