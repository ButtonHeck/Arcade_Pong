package com.buttonHeck.pong.items;

import com.buttonHeck.pong.controllers.ImageController;
import com.buttonHeck.pong.Game;
import javafx.scene.image.Image;

public class PlusScore extends Item {

    PlusScore(Image image, int ordinal) {
        super(image, ordinal);
    }

    @Override
    public void applyAction() {
        Game.ItemHandler.plusScore();
    }

    @Override
    public Item copy() {
        return new PlusScore(ImageController.getItemImage(3), 3);
    }
}
