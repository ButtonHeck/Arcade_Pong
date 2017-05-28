package com.buttonHeck.pong.items;

import com.buttonHeck.pong.controllers.ImageController;
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
                return new BatSizeIncrease(ImageController.getItemImage(0), 0);
            case 1:
                return new BatSizeDecrease(ImageController.getItemImage(1), 1);
            case 2:
                return new TimeStopper(ImageController.getItemImage(2), 2);
            case 3:
                return new PlusScore(ImageController.getItemImage(3), 3);
            case 4:
                return new SwapBats(ImageController.getItemImage(4), 4);
            case 5:
                return new BatSpeedIncrease(ImageController.getItemImage(5), 5);
            case 6:
                return new BatSpeedDecrease(ImageController.getItemImage(6), 6);
            case 7:
                return new BallSpeedUp(ImageController.getItemImage(7), 7);
            case 8:
                return new BallSpeedDown(ImageController.getItemImage(8), 8);
            case 9:
                return new Pistol(ImageController.getItemImage(9), 9);
        }
        return null;
    }
}
