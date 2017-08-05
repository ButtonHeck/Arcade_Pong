package com.buttonHeck.pong.ai;

import com.buttonHeck.pong.handler.ImageHandler;
import com.buttonHeck.pong.util.HelperMethods;
import com.buttonHeck.pong.util.Options;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Random;

import static com.buttonHeck.pong.Game.HEIGHT;
import static com.buttonHeck.pong.Game.DEFAULT_PADDLE_HEIGHT;
import static com.buttonHeck.pong.util.HelperMethods.*;
import static java.lang.Math.random;

public class AI extends Rectangle {

    private int moveDelayCount;
    private Random random;
    private Image[] score;
    private int missesLeft = 4;
    private boolean timeStopper;
    private boolean timeStopRequest;
    private double timeStopDuration;
    private boolean projectileHit;
    private double projectileHitDuration;
    private boolean hasPistol;
    private boolean shootRequest;
    private Circle[] aiShots;
    private int shotsLeft;
    private ArrayList<Image> itemsPicked;

    public AI(double width, double height) {
        super(width, height);
        initializeVariables();
        initializeProjectiles();
        setEffect(new GaussianBlur(2));
        setFill(Color.TOMATO.brighter());
    }

    private void initializeVariables() {
        random = new Random();
        itemsPicked = new ArrayList<>();
        score = new Image[]{
                ImageHandler.getSwitchImage(true),
                ImageHandler.getSwitchImage(true),
                ImageHandler.getSwitchImage(true),
                ImageHandler.getSwitchImage(true),
                ImageHandler.getSwitchImage(true)
        };
        resetObjects();
    }

    public void resetObjects() {
        itemsPicked.clear();
        timeStopper = false;
        timeStopRequest = false;
        timeStopDuration = 2.0;
        hasPistol = false;
        shotsLeft = 3;
        shootRequest = false;
        moveDelayCount = 22;
        projectileHit = false;
        projectileHitDuration = 0.5;
        setHeight(DEFAULT_PADDLE_HEIGHT);
    }

    private void initializeProjectiles() {
        aiShots = new Circle[]{
                new Circle(4),
                new Circle(4),
                new Circle(4)
        };
        for (Circle aiShot : aiShots) {
            aiShot.setFill(Color.WHITE);
            aiShot.setEffect(new GaussianBlur(2));
        }
    }

    public final void move(ImageView ball, double aiActualSpeed, double aiBasicSpeed, double ballDY) {
        if (needNotMove(ball)) return;
        if (canNotMove()) return;
        if (gotConfused()) return;
        if (yOf(this) - halfHeightOf(this) + halfHeightOf(ball) + aiActualSpeed >= 0
                || yOf(this) + heightOf(this) + aiActualSpeed <= HEIGHT - 1)
            HelperMethods.setY(this, (yOf(this) + (yOf(this) + halfHeightOf(this) - halfHeightOf(ball) > yOf(ball)
                    ? (aiBasicSpeed >= ballDY && centralizedWithBall(ball) ? -ballDY : -aiBasicSpeed)
                    : (aiBasicSpeed >= ballDY && centralizedWithBall(ball) ? ballDY : aiBasicSpeed))));
        checkTimeStopRequest(ball, aiBasicSpeed, ballDY);
        checkShootRequest();
    }

    private boolean needNotMove(ImageView ball) {
        return (yOf(ball) - halfHeightOf(this) <= 0
                && yOf(this) <= halfHeightOf(ball))
                || (heightOf(ball) + yOf(ball) + halfHeightOf(this) >= HEIGHT - halfHeightOf(ball)
                && yOf(this) + heightOf(this) >= HEIGHT - halfHeightOf(ball));
    }

    private boolean canNotMove() {
        if (moveDelayCount < 22) {
            moveDelayCount += random.nextInt(4) + 1 + Options.getDifficulty().opponentMoveDelaySpeedUp;
            return true;
        }
        return false;
    }

    private boolean gotConfused() {
        if (random.nextInt(75) == 0) {
            moveDelayCount = 0;
            return true;
        }
        return false;
    }

    private void checkTimeStopRequest(ImageView ball, double aiBasicSpeed, double ballDY) {
        if (aiBasicSpeed - 1 <= ballDY
                && timeStopper
                && !centralizedWithBall(ball)
                && Math.abs(xOf(ball) - xOf(this)) <= 40)
            timeStopRequest = true;
    }

    private void checkShootRequest() {
        if (hasPistol && random.nextInt(512) == 0)
            shootRequest = true;
    }

    private boolean centralizedWithBall(ImageView ball) {
        return Math.abs(yOf(this) + halfHeightOf(this) - (yOf(ball) + halfHeightOf(ball))) < heightOf(ball) * (random() + 1);
    }

    public void ballMissed() {
        score[missesLeft] = ImageHandler.getSwitchImage(false);
    }

    public void decreaseMissesLeft() {
        --missesLeft;
    }

    public void resetScore() {
        missesLeft = 4;
        for (int i = 0; i < score.length; i++)
            score[i] = ImageHandler.getSwitchImage(true);
    }

    public void resetPistol() {
        hasPistol = false;
        shootRequest = false;
        shotsLeft = 3;
        itemsPicked.remove(ImageHandler.getItemImage(9));
    }

    public boolean isConfusedYet(double updateSpeed) {
        if (projectileHit) {
            projectileHitDuration -= updateSpeed;
            if (projectileHitDuration <= 0) {
                projectileHitDuration = 0.5;
                projectileHit = false;
            }
            return true;
        }
        return false;
    }

    //Getters

    public int getMissesLeft() {
        return missesLeft;
    }

    public Image[] getScore() {
        return score;
    }

    public ArrayList<Image> getItemsPicked() {
        return itemsPicked;
    }

    public double getTimeStopDuration() {
        return timeStopDuration;
    }

    public boolean isTimeStopRequested() {
        return timeStopRequest;
    }

    public boolean isShootRequest() {
        return shootRequest;
    }

    public boolean hasPistol() {
        return hasPistol;
    }

    public Circle[] getAiShots() {
        return aiShots;
    }

    public int getShotsLeft() {
        return shotsLeft;
    }

    public void setTimeStopper(boolean has) {
        timeStopper = has;
    }

    public void disableTimeStopRequest() {
        this.timeStopRequest = false;
    }

    //Setters

    public void setTimeStopDuration(double timeStopDuration) {
        this.timeStopDuration = timeStopDuration;
    }

    public void setMissesLeft(int missesLeft) {
        this.missesLeft = missesLeft;
    }

    public void setProjectileHit(boolean wasHit) {
        projectileHit = wasHit;
    }

    public void setHasPistol(boolean hasPistol) {
        this.hasPistol = hasPistol;
    }

    public void setShootRequest(boolean shootRequest) {
        this.shootRequest = shootRequest;
    }

    public void setShotsLeft(int shotsLeft) {
        this.shotsLeft = shotsLeft;
    }
}
