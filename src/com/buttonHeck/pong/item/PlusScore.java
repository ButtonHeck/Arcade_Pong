package com.buttonHeck.pong.item;

import com.buttonHeck.pong.handler.ImageHandler;
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
        return new PlusScore(ImageHandler.getItemImage(3), 3);
    }
}
