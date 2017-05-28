package com.buttonHeck.pong.util;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public abstract class HelperMethods {

    public static double widthOf(Node node) {
        return node.getLayoutBounds().getWidth();
    }

    public static double heightOf(Node node) {
        return node.getLayoutBounds().getHeight();
    }

    public static double halfWidthOf(Node node) {
        return node.getLayoutBounds().getWidth() / 2;
    }

    public static double halfHeightOf(Node node) {
        return node.getLayoutBounds().getHeight() / 2;
    }

    public static double xOf(Node node) {return node.getTranslateX();}

    public static double yOf(Node node) {
        return node.getTranslateY();
    }

    public static void setX(Node node, double x) {
        node.setTranslateX(x);
    }

    public static void setY(Node node, double y) {
        node.setTranslateY(y);
    }
}
