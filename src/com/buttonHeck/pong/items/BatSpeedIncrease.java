package com.buttonHeck.pong.items;

import com.buttonHeck.pong.Game;
import com.buttonHeck.pong.controllers.ImageController;
import javafx.scene.image.Image;

public class BatSpeedIncrease extends Item {

    BatSpeedIncrease(Image image, int ordinal) {
        super(image, ordinal);
    }

    @Override
    public void applyAction() {
        Game.ItemHandler.changeBatSpeed(true);
    }

    @Override
    public Item copy() {
        return new BatSpeedIncrease(ImageController.getItemImage(5), 5);
    }
}
