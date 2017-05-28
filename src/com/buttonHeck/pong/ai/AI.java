package com.buttonHeck.pong.ai;

import com.buttonHeck.pong.Game;
import com.buttonHeck.pong.controllers.ImageController;
import com.buttonHeck.pong.util.Options;
import com.buttonHeck.pong.util.HelperMethods;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Random;

import static com.buttonHeck.pong.util.HelperMethods.*;

public class AI extends Rectangle {

    private int updateCountdown;
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
        random = new Random();
        timeStopper = false;
        timeStopRequest = false;
        timeStopDuration = 2.0;
        updateCountdown = 22;
        projectileHit = false;
        projectileHitDuration = 0.5;
        shootRequest = false;
        shotsLeft = 3;
        aiShots = new Circle[]{
                new Circle(4),
                new Circle(4),
                new Circle(4)
        };
        for (Circle aiShot : aiShots) {
            aiShot.setFill(Color.WHITE);
            aiShot.setEffect(new GaussianBlur(2));
        }
        score = new Image[]{
                ImageController.getSwitchImage(true),
                ImageController.getSwitchImage(true),
                ImageController.getSwitchImage(true),
                ImageController.getSwitchImage(true),
                ImageController.getSwitchImage(true)
        };
        setEffect(new GaussianBlur(2));
        itemsPicked = new ArrayList<>();
    }

    public final void move(ImageView ball, double aiActualSpeed, double aiBasicSpeed, double ballDY) {
        if ((yOf(ball) - halfHeightOf(this) <= 0 && yOf(this) <= halfHeightOf(ball))
                || (heightOf(ball) + yOf(ball) + halfHeightOf(this) >= Game.HEIGHT - halfHeightOf(ball) && yOf(this) + heightOf(this) >= Game.HEIGHT - halfHeightOf(ball)))
            return;
        if (updateCountdown < 22) {
            updateCountdown += random.nextInt(4) + 1 + Options.getDifficulty().opponentUpdateSpeedUp;
            return;
        }
        if (random.nextInt(75) == 0) {
            updateCountdown = 0;
            return;
        }
        if (yOf(this) - halfHeightOf(this) + halfHeightOf(ball) + aiActualSpeed >= 0 || yOf(this) + heightOf(this) + aiActualSpeed <= Game.HEIGHT - 1)
            HelperMethods.setY(this, (yOf(this) + (yOf(this) + halfHeightOf(this) - halfHeightOf(ball) > yOf(ball) ?
                    (aiBasicSpeed >= ballDY && centralizedWithBall(ball) ? -ballDY : -aiBasicSpeed) :
                    (aiBasicSpeed >= ballDY && centralizedWithBall(ball) ? ballDY : aiBasicSpeed))));
        if (aiBasicSpeed - 1 <= ballDY && timeStopper && !centralizedWithBall(ball) && Math.abs(xOf(ball) - xOf(this)) <= 40)
            timeStopRequest = true;
        if (hasPistol && random.nextInt(512) == 0)
            shootRequest = true;
    }

    private boolean centralizedWithBall(ImageView ball) {
        return Math.abs(yOf(this) + halfHeightOf(this) - (yOf(ball) + halfHeightOf(ball))) < heightOf(ball) * (Math.random() + 1);
    }

    public Image[] getScore() {
        return score;
    }

    public void ballMissed() {
        score[missesLeft] = ImageController.getSwitchImage(false);
    }

    public int getMissesLeft() {
        return missesLeft;
    }

    public void renewMissCount() {
        --missesLeft;
    }

    public void resetScore() {
        missesLeft = 4;
        for (int i = 0; i < score.length; i++)
            score[i] = ImageController.getSwitchImage(true);
    }

    public void resetObjects() {
        itemsPicked.clear();
        resetTimeStopRequest();
        setTimeStopper(false);
        setTimeStopDuration(2.0);
        setHasPistol(false);
        setShotsLeft(3);
        setShootRequest(false);
    }

    public void setTimeStopper(boolean has) {
        timeStopper = has;
    }

    public boolean timeStopRequested() {
        return timeStopRequest;
    }

    public void resetTimeStopRequest() {
        this.timeStopRequest = false;
    }

    public double getTimeStopDuration() {
        return timeStopDuration;
    }

    public void setTimeStopDuration(double timeStopDuration) {
        this.timeStopDuration = timeStopDuration;
    }

    public void setMissesLeft(int missesLeft) {
        this.missesLeft = missesLeft;
    }

    public void projectileHit(boolean wasHit) {
        projectileHit = wasHit;
    }

    public boolean isProjectileHit() {
        return projectileHit;
    }

    public double getProjectileHitDuration() {
        return projectileHitDuration;
    }

    public void setProjectileHitDuration(double projectileHitDuration) {
        this.projectileHitDuration = projectileHitDuration;
    }

    public void setHasPistol(boolean hasPistol) {
        this.hasPistol = hasPistol;
    }

    public boolean hasPistol() {
        return hasPistol;
    }

    public boolean isShootRequest() {
        return shootRequest;
    }

    public void setShootRequest(boolean shootRequest) {
        this.shootRequest = shootRequest;
    }

    public Circle[] getAiShots() {
        return aiShots;
    }

    public int getShotsLeft() {
        return shotsLeft;
    }

    public void setShotsLeft(int shotsLeft) {
        this.shotsLeft = shotsLeft;
    }

    public ArrayList<Image> getItemsPicked() {
        return itemsPicked;
    }
}
