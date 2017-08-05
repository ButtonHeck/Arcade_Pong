package com.buttonHeck.pong.item;

import com.buttonHeck.pong.Game;
import com.buttonHeck.pong.handler.ImageHandler;
import javafx.scene.image.Image;

public class BallSpeedItem extends Item {

    private final boolean speedUp;

    BallSpeedItem(Image image, int ordinal, boolean speedUp) {
        super(image, ordinal);
        this.speedUp = speedUp;
    }

    @Override
    public void applyAction() {
        Game.ItemHandler.setBallSpeedScale(speedUp ? 1.12 : 0.88);
    }

    @Override
    public Item copy() {
        return new BallSpeedItem(ImageHandler.getItemImage(speedUp ? 7 : 8), speedUp ? 7 : 8, speedUp);
    }
}
