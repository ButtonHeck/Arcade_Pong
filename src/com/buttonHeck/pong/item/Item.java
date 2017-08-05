package com.buttonHeck.pong.item;

import com.buttonHeck.pong.handler.ImageHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Item extends ImageView {

    public final int ordinal;

    Item(Image image, int ordinal) {
        super(image);
        this.ordinal = ordinal;
    }

    public abstract Item copy();

    public abstract void applyAction();

    public static Item getItemByOrdinal(int ordinal) {
        switch (ordinal) {
            case 0:
                return new BatSizeItem(ImageHandler.getItemImage(0), 0, true);
            case 1:
                return new BatSizeItem(ImageHandler.getItemImage(1), 1, false);
            case 2:
                return new TimeStopper(ImageHandler.getItemImage(2), 2);
            case 3:
                return new PlusScore(ImageHandler.getItemImage(3), 3);
            case 4:
                return new SwapBats(ImageHandler.getItemImage(4), 4);
            case 5:
                return new BatSpeedItem(ImageHandler.getItemImage(5), 5, true);
            case 6:
                return new BatSpeedItem(ImageHandler.getItemImage(6), 6, false);
            case 7:
                return new BallSpeedItem(ImageHandler.getItemImage(7), 7, true);
            case 8:
                return new BallSpeedItem(ImageHandler.getItemImage(8), 8, true);
            case 9:
                return new Pistol(ImageHandler.getItemImage(9), 9);
        }
        return null;
    }
}
