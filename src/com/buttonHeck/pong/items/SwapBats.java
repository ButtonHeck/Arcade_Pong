package com.buttonHeck.pong.items;

import com.buttonHeck.pong.Game;
import com.buttonHeck.pong.controllers.ImageController;
import javafx.scene.image.Image;

public class SwapBats extends Item {

    SwapBats(Image image, int ordinal) {
        super(image, ordinal);
    }

    @Override
    public void applyAction() {
        Game.ItemHandler.swapBats();
    }

    @Override
    public Item copy() {
        return new SwapBats(ImageController.getItemImage(4), 4);
    }
}
