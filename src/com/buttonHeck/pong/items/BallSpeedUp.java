package com.buttonHeck.pong.items;

import com.buttonHeck.pong.Game;
import com.buttonHeck.pong.controllers.ImageController;
import javafx.scene.image.Image;

public class BallSpeedUp extends Item {

    BallSpeedUp(Image image, int ordinal) {
        super(image, ordinal);
    }

    @Override
    public void applyAction() {
        Game.ItemHandler.setBallSpeedUpScale(1.12);
    }

    @Override
    public Item copy() {
        return new BallSpeedUp(ImageController.getItemImage(7), 7);
    }
}
