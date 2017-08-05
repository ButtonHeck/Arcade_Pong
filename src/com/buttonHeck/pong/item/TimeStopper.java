package com.buttonHeck.pong.items;

import com.buttonHeck.pong.Game;
import com.buttonHeck.pong.handler.ImageHandler;
import javafx.scene.image.Image;

public class TimeStopper extends Item {

    TimeStopper(Image image, int ordinal) {
        super(image, ordinal);
    }

    @Override
    public void applyAction() {
        Game.ItemHandler.timeStop();
    }

    @Override
    public Item copy() {
        return new TimeStopper(ImageHandler.getItemImage(2), 2);
    }
}
