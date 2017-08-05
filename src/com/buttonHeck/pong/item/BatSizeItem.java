package com.buttonHeck.pong.item;

import com.buttonHeck.pong.Game;
import com.buttonHeck.pong.handler.ImageHandler;
import javafx.scene.image.Image;

public class BatSizeDecrease extends Item {

    BatSizeDecrease(Image image, int ordinal) {
        super(image, ordinal);
    }

    @Override
    public void applyAction() {
        Game.ItemHandler.batResize(false);
    }

    @Override
    public Item copy() {
        return new BatSizeDecrease(ImageHandler.getItemImage(1), 1);
    }
}