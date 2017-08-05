package com.buttonHeck.pong.item;

import com.buttonHeck.pong.Game;
import com.buttonHeck.pong.handler.ImageHandler;
import javafx.scene.image.Image;

public class Pistol extends Item {

    Pistol(Image image, int ordinal) {
        super(image, ordinal);
    }

    @Override
    public void applyAction() {
        Game.ItemHandler.addPistol();
    }

    @Override
    public Item copy() {
        return new Pistol(ImageHandler.getItemImage(9), 9);
    }
}
