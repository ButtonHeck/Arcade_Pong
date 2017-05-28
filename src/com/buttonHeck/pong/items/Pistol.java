package com.buttonHeck.pong.items;

import com.buttonHeck.pong.Game;
import com.buttonHeck.pong.controllers.ImageController;
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
        return new Pistol(ImageController.getItemImage(9), 9);
    }
}
