package com.buttonHeck.pong.items;

import com.buttonHeck.pong.Game;
import com.buttonHeck.pong.controllers.ImageController;
import javafx.scene.image.Image;

public class BatSizeIncrease extends Item {

    BatSizeIncrease(Image image, int ordinal) {
        super(image, ordinal);
    }

    @Override
    public void applyAction() {
        Game.ItemHandler.batResize(true);
    }

    @Override
    public Item copy() {
        return new BatSizeIncrease(ImageController.getItemImage(0), 0);
    }
}
