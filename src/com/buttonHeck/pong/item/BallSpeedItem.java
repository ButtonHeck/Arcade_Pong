package com.buttonHeck.pong.item;

import com.buttonHeck.pong.Game;
import com.buttonHeck.pong.handler.ImageHandler;
import javafx.scene.image.Image;

public class BallSpeedDown extends Item {

    BallSpeedDown(Image image, int ordinal) {
        super(image, ordinal);
    }

    @Override
    public void applyAction() {
        Game.ItemHandler.setBallSpeedUpScale(0.88);
    }

    @Override
    public Item copy() {
        return new BallSpeedDown(ImageHandler.getItemImage(8), 8);
    }
}
