package com.buttonHeck.pong.item;

import com.buttonHeck.pong.Game;
import com.buttonHeck.pong.handler.ImageHandler;
import javafx.scene.image.Image;

public class BatSizeItem extends Item {

    private final boolean sizeUp;

    BatSizeItem(Image image, int ordinal, boolean sizeUp) {
        super(image, ordinal);
        this.sizeUp = sizeUp;
    }

    @Override
    public void applyAction() {
        Game.ItemHandler.batResize(sizeUp);
    }

    @Override
    public Item copy() {
        return new BatSizeItem(ImageHandler.getItemImage(sizeUp ? 0 : 1), sizeUp ? 0 : 1, sizeUp);
    }
}
