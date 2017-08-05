package com.buttonHeck.pong.item;

import com.buttonHeck.pong.Game;
import com.buttonHeck.pong.handler.ImageHandler;
import javafx.scene.image.Image;

public class BatSpeedItem extends Item {

    private final boolean speedUp;

    BatSpeedItem(Image image, int ordinal, boolean speedUp) {
        super(image, ordinal);
        this.speedUp = speedUp;
    }

    @Override
    public void applyAction() {
        Game.ItemHandler.changeBatSpeed(speedUp);
    }

    @Override
    public Item copy() {
        return new BatSpeedItem(ImageHandler.getItemImage(speedUp ? 5 : 6), speedUp ? 5 : 6, speedUp);
    }
}
