package com.buttonHeck.pong.items;

import com.buttonHeck.pong.Game;
import com.buttonHeck.pong.controllers.ImageController;
import javafx.scene.image.Image;

public class BatSpeedDecrease extends Item {

    BatSpeedDecrease(Image image, int ordinal) {
        super(image, ordinal);
    }

    @Override
    public void applyAction() {
        Game.ItemHandler.changeBatSpeed(false);
    }

    @Override
    public Item copy() {
        return new BatSpeedDecrease(ImageController.getItemImage(6), 6);
    }
}
