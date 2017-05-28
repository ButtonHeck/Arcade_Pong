package com.buttonHeck.pong;

import com.buttonHeck.pong.controllers.AudioController;
import com.buttonHeck.pong.controllers.ButtonController;
import com.buttonHeck.pong.controllers.ImageController;
import com.buttonHeck.pong.controllers.TextController;
import com.buttonHeck.pong.items.Item;
import com.buttonHeck.pong.util.Options;
import com.buttonHeck.pong.ai.AI;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static com.buttonHeck.pong.util.HelperMethods.*;

public class Game extends Application {

    //Application variables
    public static final int WIDTH = 1024, HEIGHT = WIDTH * 3 / 4, SIZE_SCALE = 1;
    private static final double BALL_REFLECT_RATIO = 38.5;
    private static Stage stage;
    private static boolean upKey, downKey, shiftKey;
    private static Allocator allocator;
    private static Random random;
    private static double updateSpeed = 0.0018;
    private static Text resultMessageWin, resultMessageLose;
    private static Timeline countdownTimeline;
    private static Text countdownText;
    private static final int countdownNumber[] = {3};
    private static Timeline countdownTextTimeline;
    private static final int countdownFontSize[] = {96 * 4};
    private static Point2D screenCenter;

    static {
        resultMessageWin = TextController.getResultMessage(true);
        resultMessageLose = TextController.getResultMessage(false);
        countdownText = TextController.getCountdown();
        allocator = new Allocator();
        random = new Random();
        countdownTimeline = new Timeline();
        countdownTextTimeline = new Timeline();
        screenCenter = new Point2D(WIDTH / 2, HEIGHT / 2);
    }

    //Menu scene variables
    private static Scene menuScene;
    private static Group menuRoot;
    private static Canvas itemsBackground = new Canvas(WIDTH * 0.9, HEIGHT / 1.65);
    private static ImageView startBtn, exitBtn, musicSwc, soundsSwc, itemsSwc, easyBtn, mediumBtn, hardBtn;
    private static ImageView[] menuItems = new ImageView[10];
    private static Text[] menuTexts = TextController.getTexts();
    private static Text[] itemsTexts = TextController.getItemsTexts();
    private static Text author = TextController.getAuthorText();

    //Game scene variables
    static Scene gameScene;
    private static VBox gameRoot = new VBox();
    private static Group gameGroup = new Group();
    private static Canvas gameCanvas, infoCanvas;
    private static double winRateCoords[] = new double[22];
    private static Timeline gameTimeline = new Timeline();
    private static final double BAT_HEIGHT = HEIGHT / 8;
    private static GaussianBlur blur = new GaussianBlur(2);
    private static ImageView ball;
    private static ImageView[] ballTail;
    private static int ballTailIndex, ballTailUpdate;
    private static ImageView magnetHigh, magnetLow;
    private static ImageView blackhole1, blackhole2;

    //Game logic variables
    private static boolean ballLeft, ballUp;
    private static double ballDX = 1.5, ballDY = 1.075;
    private static double speedUpRatio = 1.0; //changes when ball speed up/down item is picked
    private static boolean batsSwapped;
    private static boolean playersBallTurn = true;
    private static boolean magnetHighLeft = true, magnetLowLeft = false;
    private static boolean[] itemsInGame = new boolean[10];
    private static ArrayList<Item> itemsInGroup = new ArrayList<>();
    private static double playerSpeedScale, aiSpeedScale;
    //used in space level
    private static double blackholeRadius = 210;
    private static double sinDegree = 0;
    private static boolean blackholeActive = true;

    //Level chooser scene
    private static Scene levelChooserScene;
    private static Text[] levelTexts = TextController.getLevelTexts();
    private static ImageView classicLevelBtn, magneticLevelBtn, spaceLevelBtn, returnBtn;

    //Player variables
    private static Rectangle player;
    private static double playerBasicSpeed = ballDY * 1.2;
    private static int missesLeft;
    private static Image score[] = new Image[]{
            ImageController.getSwitchImage(true),
            ImageController.getSwitchImage(true),
            ImageController.getSwitchImage(true),
            ImageController.getSwitchImage(true),
            ImageController.getSwitchImage(true)
    };
    private static ArrayList<Image> itemsPicked;
    private static boolean hasTimeStopper, timeStopped;
    private static double timeStopDuration = 2;
    private static Text timeStopDurationText = TextController.getTimeStopDurationText();
    private static boolean hasPistol;
    private static int pistolsLeft;
    private static Circle playerShots[] = new Circle[]{
            new Circle(4),
            new Circle(4),
            new Circle(4)
    };
    private static boolean projectileHit;
    private static double projectileHitDuration;

    //AI variables
    private static AI ai;
    private static Circle[] aiShots;
    private static double aiBasicSpeed = playerBasicSpeed;
    private static double aiActualSpeed = ballDY * aiSpeedScale;

    @Override
    public void start(Stage stage) throws Exception {
        Game.stage = stage;
        initObjects();
        initGameCanvases();
        allocator.allocateObjects();
        buildGameContentTree();
        initializeTimelines();
        allocator.allocateMenuObjects();
        allocator.allocateLevelButtons();
        buildMenuContentTree();
        resetCountdown();
        resetScore();
        launchGame();
        initializeKeyboardHandler();
        AudioController.playMusic();
    }

    private void initObjects() {
        player = new Rectangle(8, BAT_HEIGHT);
        player.setFill(Color.LIGHTBLUE);
        player.setEffect(blur);
        ai = new AI(8, BAT_HEIGHT);
        ai.setFill(Color.TOMATO.brighter());
        itemsPicked = new ArrayList<>();
        aiShots = ai.getAiShots();
        ball = new ImageView(ImageController.getBallImage());
        ballTail = new ImageView[]{
                new ImageView(ImageController.getBallImage()),
                new ImageView(ImageController.getBallImage()),
                new ImageView(ImageController.getBallImage()),
                new ImageView(ImageController.getBallImage()),
                new ImageView(ImageController.getBallImage())
        };
        ballTailIndex = 0;
        ballTailUpdate = 0;
        for (ImageView tailPiece : ballTail) {
            tailPiece.setOpacity(0.1);
        }

        magnetHigh = new ImageView(ImageController.getMagnetImage());
        magnetLow = new ImageView(ImageController.getMagnetImage());
        magnetLow.getTransforms().add(new Rotate(180));

        blackhole1 = new ImageView(ImageController.getBlackholeImage());
        blackhole1.setEffect(blur);
        blackhole2 = new ImageView(ImageController.getBlackholeImage());
        blackhole2.setEffect(blur);

        for (int i = 0; i < menuItems.length; i++)
            menuItems[i] = ButtonController.getItem(i);
        initMenuObjects();
        initSelectObjects();
    }

    private void initMenuObjects() {
        startBtn = ButtonController.getStartButton();
        exitBtn = ButtonController.getExitButton();
        musicSwc = ButtonController.getMusicSwitch();
        soundsSwc = ButtonController.getSoundsSwitch();
        itemsSwc = ButtonController.getItemsSwitch();
        easyBtn = ButtonController.getEasyButton();
        mediumBtn = ButtonController.getMediumButton();
        hardBtn = ButtonController.getHardButton();
    }

    private void initSelectObjects() {
        classicLevelBtn = ButtonController.getClassicLevelBtn();
        magneticLevelBtn = ButtonController.getMagneticLevelBtn();
        spaceLevelBtn = ButtonController.getSpaceLevelBtn();
        returnBtn = ButtonController.getReturnBtn();
        Group levelRoot = new Group();
        levelRoot.getChildren().addAll(classicLevelBtn, magneticLevelBtn, spaceLevelBtn, returnBtn);
        for (Text text : levelTexts)
            levelRoot.getChildren().add(text);
        levelChooserScene = new Scene(levelRoot, WIDTH, HEIGHT, Color.BLACK);
    }

    private void initGameCanvases() {
        gameCanvas = new Canvas(WIDTH * SIZE_SCALE, HEIGHT * SIZE_SCALE);
        gameCanvas.getGraphicsContext2D().setFill(Color.BLACK);
        gameCanvas.getGraphicsContext2D().fillRect(0, 0, widthOf(gameCanvas), heightOf(gameCanvas));
        infoCanvas = new Canvas(WIDTH * SIZE_SCALE, HEIGHT * SIZE_SCALE / 15);
        infoCanvas.getGraphicsContext2D().setFill(Color.BLACK.brighter());
        infoCanvas.getGraphicsContext2D().fillRect(0, 0, infoCanvas.getWidth(), infoCanvas.getHeight());
        itemsBackground.setOpacity(0.3);
        itemsBackground.getGraphicsContext2D().setFill(Color.DARKSLATEBLUE);
        itemsBackground.getGraphicsContext2D().fillRect(0, 0, widthOf(itemsBackground), heightOf(itemsBackground));
        itemsBackground.setEffect(new GaussianBlur(20));
        renewInfo();
    }

    private static void renewInfo() {
        if (!batsSwapped) {
            winRateCoords[0] = 10;
            winRateCoords[1] = 55;
            winRateCoords[2] = 100;
            winRateCoords[3] = 145;
            winRateCoords[4] = 190;
            winRateCoords[5] = 235;
            winRateCoords[6] = 280;
            winRateCoords[7] = 325;
            winRateCoords[8] = 370;
            winRateCoords[9] = 415;
            winRateCoords[10] = 460;
            winRateCoords[11] = widthOf(infoCanvas) - 50;
            winRateCoords[12] = widthOf(infoCanvas) - 95;
            winRateCoords[13] = widthOf(infoCanvas) - 140;
            winRateCoords[14] = widthOf(infoCanvas) - 185;
            winRateCoords[15] = widthOf(infoCanvas) - 230;
            winRateCoords[16] = widthOf(infoCanvas) - 275;
            winRateCoords[17] = widthOf(infoCanvas) - 320;
            winRateCoords[18] = widthOf(infoCanvas) - 365;
            winRateCoords[19] = widthOf(infoCanvas) - 410;
            winRateCoords[20] = widthOf(infoCanvas) - 455;
            winRateCoords[21] = widthOf(infoCanvas) - 500;
        } else {
            winRateCoords[11] = 10;
            winRateCoords[12] = 55;
            winRateCoords[13] = 100;
            winRateCoords[14] = 145;
            winRateCoords[15] = 190;
            winRateCoords[16] = 235;
            winRateCoords[17] = 280;
            winRateCoords[18] = 325;
            winRateCoords[19] = 370;
            winRateCoords[20] = 415;
            winRateCoords[21] = 460;
            winRateCoords[0] = widthOf(infoCanvas) - 50;
            winRateCoords[1] = widthOf(infoCanvas) - 95;
            winRateCoords[2] = widthOf(infoCanvas) - 140;
            winRateCoords[3] = widthOf(infoCanvas) - 185;
            winRateCoords[4] = widthOf(infoCanvas) - 230;
            winRateCoords[5] = widthOf(infoCanvas) - 275;
            winRateCoords[6] = widthOf(infoCanvas) - 320;
            winRateCoords[7] = widthOf(infoCanvas) - 365;
            winRateCoords[8] = widthOf(infoCanvas) - 410;
            winRateCoords[9] = widthOf(infoCanvas) - 455;
            winRateCoords[10] = widthOf(infoCanvas) - 500;
        }
        infoCanvas.getGraphicsContext2D().setFill(Color.BLACK.brighter());
        infoCanvas.getGraphicsContext2D().fillRect(235, 0, 512 + 45, infoCanvas.getHeight());
        for (int i = 0; i < itemsPicked.size(); i++)
            infoCanvas.getGraphicsContext2D().drawImage(itemsPicked.get(i), winRateCoords[i + 5], 5);
        for (int i = 0; i < ai.getItemsPicked().size(); i++)
            infoCanvas.getGraphicsContext2D().drawImage(ai.getItemsPicked().get(i), winRateCoords[i + 16], 5);
        infoCanvas.getGraphicsContext2D().drawImage(score[0], winRateCoords[0], 5);
        infoCanvas.getGraphicsContext2D().drawImage(score[1], winRateCoords[1], 5);
        infoCanvas.getGraphicsContext2D().drawImage(score[2], winRateCoords[2], 5);
        infoCanvas.getGraphicsContext2D().drawImage(score[3], winRateCoords[3], 5);
        infoCanvas.getGraphicsContext2D().drawImage(score[4], winRateCoords[4], 5);
        infoCanvas.getGraphicsContext2D().drawImage(ai.getScore()[0], winRateCoords[11], 5);
        infoCanvas.getGraphicsContext2D().drawImage(ai.getScore()[1], winRateCoords[12], 5);
        infoCanvas.getGraphicsContext2D().drawImage(ai.getScore()[2], winRateCoords[13], 5);
        infoCanvas.getGraphicsContext2D().drawImage(ai.getScore()[3], winRateCoords[14], 5);
        infoCanvas.getGraphicsContext2D().drawImage(ai.getScore()[4], winRateCoords[15], 5);
    }

    private void buildGameContentTree() {
        gameGroup.getChildren().addAll(gameCanvas, ball, player, ai);
        for (ImageView tailPiece : ballTail)
            gameGroup.getChildren().add(tailPiece);
        gameRoot.getChildren().add(gameGroup);
        gameRoot.getChildren().add(infoCanvas);
        gameScene = new Scene(gameRoot, WIDTH, HEIGHT + heightOf(infoCanvas));
    }

    private void initializeTimelines() {
        initGameTimeline();
        initCountdownTimeline();
    }

    private void initGameTimeline() {
        KeyFrame gameFrame = new KeyFrame(Duration.seconds(updateSpeed), event -> {
            if (aiTimeStopEvent()) return;
            playerMoveEvent();
            updateEvent();
            if (gameGroup.getChildren().contains(magnetHigh))
                magnetEvent();
            if (gameGroup.getChildren().contains(blackhole1))
                blackholeEvent();
        });
        gameTimeline.getKeyFrames().add(gameFrame);
        gameTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    private boolean aiTimeStopEvent() {
        if (ai.timeStopRequested()) {
            ai.setTimeStopper(false);
            ai.getItemsPicked().remove(ImageController.getItemImage(2));
            renewInfo();
            if (!gameGroup.getChildren().contains(timeStopDurationText)) {
                AudioController.timeStopper();
                setX(timeStopDurationText, xOf(ball) - 15);
                setY(timeStopDurationText, (yOf(ball) >= WIDTH / 2 ? yOf(ball) - 15 : yOf(ball) + heightOf(ball) + 15));
                gameGroup.getChildren().add(timeStopDurationText);
            }
            ai.move(ball, aiActualSpeed, aiBasicSpeed * aiSpeedScale, ballDY);
            ai.setTimeStopDuration(ai.getTimeStopDuration() - updateSpeed);
            timeStopDurationText.setText(String.valueOf(ai.getTimeStopDuration()).substring(0, 4));
            timeStopDurationText.setOpacity(ai.getTimeStopDuration() / 2);
            if (ai.getTimeStopDuration() <= 0) {
                ai.resetTimeStopRequest();
                ai.setTimeStopDuration(2.0);
                gameGroup.getChildren().remove(timeStopDurationText);
                timeStopDurationText.setText(String.valueOf(2.0));
            }
            return true;
        }
        return false;
    }

    private void playerMoveEvent() {
        if (!projectileHit) {
            if (upKey) {
                if (player.getBoundsInParent().getMinY() >= playerBasicSpeed * playerSpeedScale)
                    setY(player, yOf(player) - playerBasicSpeed * playerSpeedScale * (shiftKey ? 0.5 : 1.0));
            } else if (downKey) {
                if (player.getBoundsInParent().getMaxY() <= HEIGHT - playerBasicSpeed * playerSpeedScale)
                    setY(player, yOf(player) + playerBasicSpeed * playerSpeedScale * (shiftKey ? 0.5 : 1.0));
            }
        } else {
            projectileHitDuration -= updateSpeed;
            if (projectileHitDuration <= 0) {
                projectileHit = false;
                projectileHitDuration = 0.5;
            }
        }
    }

    private void updateEvent() {
        if (!timeStopped) {
            if (++ballTailUpdate == 8) {
                setX(ballTail[Math.abs(ballTailIndex) % ballTail.length], xOf(ball));
                setY(ballTail[Math.abs(ballTailIndex) % ballTail.length], yOf(ball));
                ballTailIndex += 1;
                ballTailUpdate = 0;
            }
            if (gameGroup.getChildren().contains(magnetHigh))
                checkBallMagnet();
            if (gameGroup.getChildren().contains(blackhole1))
                checkBallBlackhole();
            checkBallBatsCollision();
            checkBallScreenCollision();
            checkBallItemCollision();
            if (ai.hasPistol() && ai.isShootRequest()) {
                if (ai.getShotsLeft() <= 0) {
                    ai.setHasPistol(false);
                    ai.setShootRequest(false);
                    ai.setShotsLeft(3);
                    ai.getItemsPicked().remove(ImageController.getItemImage(9));
                    renewInfo();
                } else {
                    ai.setShotsLeft(ai.getShotsLeft() - 1);
                    AudioController.shot();
                }
                if (ai.getShotsLeft() < 3) {
                    if (!batsSwapped)
                        setX(ai.getAiShots()[ai.getShotsLeft()], xOf(ai) - widthOf(ai.getAiShots()[ai.getShotsLeft()]) - 2);
                    else
                        setX(ai.getAiShots()[ai.getShotsLeft()], xOf(ai) + widthOf(ai) + 2);
                    setY(ai.getAiShots()[ai.getShotsLeft()], yOf(ai) + halfHeightOf(ai) - halfHeightOf(ai.getAiShots()[ai.getShotsLeft()]));
                    gameGroup.getChildren().add(ai.getAiShots()[ai.getShotsLeft()]);
                }
            }
            movePlayerProjectiles();
            moveAIProjectiles();
            if (Options.itemsOn())
                spawnItem();
            if (ai.isProjectileHit()) {
                ai.setProjectileHitDuration(ai.getProjectileHitDuration() - updateSpeed);
                if (ai.getProjectileHitDuration() <= 0) {
                    ai.setProjectileHitDuration(0.5);
                    ai.projectileHit(false);
                }
                return;
            }
            ai.move(ball, aiActualSpeed, aiBasicSpeed * aiSpeedScale, ballDY);
        } else {
            if (!gameGroup.getChildren().contains(timeStopDurationText)) {
                setX(timeStopDurationText, xOf(ball) - 15);
                setY(timeStopDurationText, (yOf(ball) >= WIDTH / 2 ? yOf(ball) - 15 : yOf(ball) + heightOf(ball) + 25));
                gameGroup.getChildren().add(timeStopDurationText);
            }
            timeStopDuration -= updateSpeed;
            timeStopDurationText.setText(String.valueOf(timeStopDuration).substring(0, 4));
            timeStopDurationText.setOpacity(timeStopDuration / 2);
            if (timeStopDuration <= 0) {
                timeStopped = false;
                timeStopDuration = 2;
                gameGroup.getChildren().remove(timeStopDurationText);
                timeStopDurationText.setText(String.valueOf(2.0));
            }
        }
    }

    private void checkBallMagnet() {
        if (ball.getBoundsInParent().getMinX() >= magnetHigh.getBoundsInParent().getMinX() &&
                ball.getBoundsInParent().getMaxX() <= magnetHigh.getBoundsInParent().getMaxX()) {
            if (ball.getBoundsInParent().getMinY() - magnetHigh.getBoundsInParent().getMinY() <= 270) {
                ballDY = Math.abs(ballDY - (ballUp ? 0.004 : -0.004));
                if (ballDY <= 0.004)
                    ballUp = !ballUp;
            }
        }
        if (ball.getBoundsInParent().getMinX() >= magnetLow.getBoundsInParent().getMinX() &&
                ball.getBoundsInParent().getMaxX() <= magnetLow.getBoundsInParent().getMaxX()) {
            if (magnetLow.getBoundsInParent().getMinY() - ball.getBoundsInParent().getMinY() <= 270) {
                ballDY = Math.abs(ballDY - (ballUp ? -0.004 : 0.004));
                if (ballDY <= 0.004)
                    ballUp = !ballUp;
            }
        }
    }

    private void checkBallBlackhole() {
        if (ball.getBoundsInParent().intersects(blackhole1.getBoundsInParent()) && blackholeActive) {
            blackholeActive = false;
            AudioController.blackhole();
            setX(ball, xOf(blackhole2) + halfWidthOf(blackhole2));
            setY(ball, yOf(blackhole2) + halfHeightOf(blackhole2));
        }
        if (ball.getBoundsInParent().intersects(blackhole2.getBoundsInParent()) && blackholeActive) {
            blackholeActive = false;
            AudioController.blackhole();
            setX(ball, xOf(blackhole1) + halfWidthOf(blackhole1));
            setY(ball, yOf(blackhole1) + halfHeightOf(blackhole1));
        }
    }

    private void checkBallBatsCollision() {
        checkBallLeftBatCollision();
        checkBallRightBatCollision();
    }

    private void checkBallLeftBatCollision() {
        Rectangle bat = batsSwapped ? ai : player;
        if (ball.getBoundsInParent().getMinX() - ballDX / 2 <= bat.getBoundsInParent().getMaxX() && ballLeft) {
            if ((yOf(ball) + halfHeightOf(ball) <= yOf(bat) + heightOf(bat) && yOf(ball) + halfHeightOf(ball) >= yOf(bat)) ||
                    ball.getBoundsInParent().intersects(bat.getBoundsInParent())) {
                setX(ball, xOf(ball) + (bat.getBoundsInParent().getMaxX() - ball.getBoundsInParent().getMinX()) - 4);
                updateAfterBatReflection(bat, !batsSwapped);
            }
        }
    }

    private void checkBallRightBatCollision() {
        Rectangle bat = batsSwapped ? player : ai;
        if (ball.getBoundsInParent().getMaxX() >= bat.getBoundsInParent().getMinX() && !ballLeft) {
            if ((yOf(ball) + halfHeightOf(ball) <= yOf(bat) + heightOf(bat) && yOf(ball) + halfHeightOf(ball) >= yOf(bat)) ||
                    ball.getBoundsInParent().intersects(bat.getBoundsInParent())) {
                setX(ball, xOf(ball) + (bat.getBoundsInParent().getMinX() - ball.getBoundsInParent().getMaxX()) + 4);
                updateAfterBatReflection(bat, batsSwapped);
            }
        }
    }

    private void updateAfterBatReflection(Node bat, boolean batsSwapped) {
        ballDY = (yOf(bat) + halfHeightOf(bat) - (yOf(ball) + halfHeightOf(ball))) / BALL_REFLECT_RATIO * speedUpRatio;
        ballUp = ballDY < 0;
        ballDY = Math.abs(ballDY);
        ballLeft = !ballLeft;
        ballDX += 0.0022;
        blackholeActive = true;
        playersBallTurn = batsSwapped;
        AudioController.batReflection();
    }

    private void checkBallScreenCollision() {
        setX(ball, xOf(ball) + (ballLeft ? -ballDX : ballDX));
        if (xOf(ball) <= ballDX + 1) {
            ballLeft = false;
            AudioController.screenReflection();
            if (!batsSwapped)
                playerMissed();
            else
                aiMissed();
        }
        if (xOf(ball) + widthOf(ball) >= WIDTH - 3) {
            ballLeft = true;
            AudioController.screenReflection();
            if (!batsSwapped)
                aiMissed();
            else
                playerMissed();
        }

        setY(ball, yOf(ball) + (ballUp ? -ballDY : ballDY));
        if (yOf(ball) <= ballDY + 1) {
            AudioController.screenReflection();
            ballUp = false;
        }
        if (yOf(ball) + heightOf(ball) + ballDY >= HEIGHT - 3) {
            AudioController.screenReflection();
            ballUp = true;
        }
    }

    private void playerMissed() {
        score[missesLeft] = ImageController.getSwitchImage(false);
        renewInfo();
        if (missesLeft == 0) {
            if (!gameGroup.getChildren().contains(resultMessageLose))
                gameGroup.getChildren().add(resultMessageLose);
            gameTimeline.stop();
            AudioController.result();
            TextController.getResultAnimation().play();
            return;
        }
        missesLeft--;
    }

    private void aiMissed() {
        ai.ballMissed();
        renewInfo();
        if (ai.getMissesLeft() == 0) {
            if (!gameGroup.getChildren().contains(resultMessageWin))
                gameGroup.getChildren().add(resultMessageWin);
            gameTimeline.stop();
            AudioController.result();
            TextController.getResultAnimation().play();
            return;
        }
        ai.renewMissCount();
    }

    private void checkBallItemCollision() {
        Iterator<Item> itemsIterator = itemsInGroup.iterator();
        while (itemsIterator.hasNext()) {
            Item currentItem = itemsIterator.next();
            if (ball.getBoundsInParent().intersects(currentItem.getBoundsInParent())) {
                currentItem.applyAction();
                itemsInGame[currentItem.ordinal] = false;
                gameGroup.getChildren().remove(currentItem);
                itemsIterator.remove();
            }
        }
    }

    private void magnetEvent() {
        if (magnetHigh.getBoundsInParent().getMinX() <= 150)
            magnetHighLeft = false;
        if (magnetHigh.getBoundsInParent().getMaxX() >= WIDTH - 150)
            magnetHighLeft = true;
        setX(magnetHigh, xOf(magnetHigh) + (magnetHighLeft ? -0.4 : 0.4));

        if (magnetLow.getBoundsInParent().getMinX() <= 150)
            magnetLowLeft = false;
        if (magnetLow.getBoundsInParent().getMaxX() >= WIDTH - 150)
            magnetLowLeft = true;
        setX(magnetLow, xOf(magnetLow) + (magnetLowLeft ? -0.36 : 0.36));
    }

    private void blackholeEvent() {
        blackhole1.setRotate(blackhole1.getRotate() + 0.2);
        blackhole2.setRotate(blackhole2.getRotate() + 0.2);
        setY(blackhole1, screenCenter.getY() + blackholeRadius * Math.sin(sinDegree));
        setX(blackhole1, screenCenter.getX() + blackholeRadius * Math.cos(sinDegree));
        setY(blackhole2, screenCenter.getY() + blackholeRadius * -Math.sin(sinDegree));
        setX(blackhole2, screenCenter.getX() + blackholeRadius * -Math.cos(sinDegree));
        sinDegree += 0.0009;
    }

    private void movePlayerProjectiles() {
        for (Circle playerShot : playerShots) {
            if (gameGroup.getChildren().contains(playerShot)) {
                if (!batsSwapped) {
                    if (playerShot.getBoundsInParent().intersects(ai.getBoundsInParent())) {
                        ai.projectileHit(true);
                        gameGroup.getChildren().remove(playerShot);
                    }
                    if (xOf(playerShot) + 1.5 >= WIDTH - 1.5) {
                        gameGroup.getChildren().remove(playerShot);
                    } else {
                        setX(playerShot, xOf(playerShot) + 1.5);
                    }
                } else {
                    if (playerShot.getBoundsInParent().intersects(ai.getBoundsInParent())) {
                        ai.projectileHit(true);
                        gameGroup.getChildren().remove(playerShot);
                    }
                    if (xOf(playerShot) <= 5) {
                        gameGroup.getChildren().remove(playerShot);
                    } else {
                        setX(playerShot, xOf(playerShot) - 1.5);
                    }
                }
            }
        }
    }

    private void moveAIProjectiles() {
        for (Circle aiShot : aiShots) {
            ai.setShootRequest(false);
            if (gameGroup.getChildren().contains(aiShot)) {
                if (!batsSwapped) {
                    if (aiShot.getBoundsInParent().intersects(player.getBoundsInParent())) {
                        projectileHit = true;
                        gameGroup.getChildren().remove(aiShot);
                    }
                    if (xOf(aiShot) <= 5) {
                        gameGroup.getChildren().remove(aiShot);
                        ai.setShootRequest(true);
                    } else {
                        setX(aiShot, xOf(aiShot) - 1.5);
                    }
                } else {
                    if (aiShot.getBoundsInParent().intersects(player.getBoundsInParent())) {
                        projectileHit = true;
                        gameGroup.getChildren().remove(aiShot);
                    }
                    if (xOf(aiShot) + 1.5 >= WIDTH - 1.5) {
                        gameGroup.getChildren().remove(aiShot);
                        ai.setShootRequest(true);
                    } else {
                        setX(aiShot, xOf(aiShot) + 1.5);
                    }
                }
            }
        }
    }

    private void spawnItem() {
        if (random.nextInt(1600) == 11) {
            int ordinal = random.nextInt(10);
            if (itemsInGame[ordinal])
                return;
            Item item = Item.getItemByOrdinal(ordinal).copy();
            item.setTranslateX(WIDTH / 2 + Math.random() * 700 - 350);
            item.setTranslateY(HEIGHT / 2 + Math.random() * 600 - 300);
            for (int i = 0; i < gameGroup.getChildren().size(); i++) {
                if (Math.abs(xOf(item) - xOf(gameGroup.getChildren().get(i))) <= widthOf(item)
                        || Math.abs(yOf(item) - yOf(gameGroup.getChildren().get(i))) <= heightOf(item))
                    return;
            }
            itemsInGroup.add(item);
            gameGroup.getChildren().add(item);
            itemsInGame[ordinal] = true;
        }
    }

    private void initCountdownTimeline() {
        KeyFrame countdownFadeout = new KeyFrame(Duration.millis(10), event -> {
            countdownFontSize[0] -= 2;
            if (countdownFontSize[0] < 192)
                countdownFontSize[0] = 192;
            countdownText.setFont(new Font("Arial", countdownFontSize[0]));
            setX(countdownText, xOf(countdownText) + 0.72);
            setY(countdownText, yOf(countdownText) - 0.72);
        });
        KeyFrame countdownFrame = new KeyFrame(Duration.seconds(1), event -> {
            if (!gameGroup.getChildren().contains(countdownText))
                gameGroup.getChildren().add(countdownText);
            AudioController.countdown();
            countdownText.setText(String.valueOf(countdownNumber[0]--));
            countdownTextTimeline.play();
            setX(countdownText, 0);
            setY(countdownText, 0);
            countdownFontSize[0] = 96 * 4;
        });
        countdownTimeline.getKeyFrames().add(countdownFrame);
        countdownTimeline.setCycleCount(4);
        countdownTextTimeline.getKeyFrames().add(countdownFadeout);
        countdownTextTimeline.setCycleCount(Animation.INDEFINITE);
    }

    private void buildMenuContentTree() {
        menuRoot = new Group();
        menuRoot.getChildren().addAll(startBtn, exitBtn, musicSwc, soundsSwc, itemsSwc,
                easyBtn, mediumBtn, hardBtn, itemsBackground, author);
        for (ImageView item : menuItems)
            menuRoot.getChildren().add(item);
        for (Text text : itemsTexts)
            menuRoot.getChildren().add(text);
        for (Text text : menuTexts)
            menuRoot.getChildren().add(text);
    }

    private void launchGame() {
        menuScene = new Scene(menuRoot, WIDTH, HEIGHT, Color.BLACK);
        stage.setScene(menuScene);
        stage.setTitle("Arcade pong");
        stage.show();
        stage.setResizable(false);
    }

    private void initializeKeyboardHandler() {
        gameScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case SHIFT:
                    shiftKey = true;
                    break;
                case W:
                    upKey = true;
                    break;
                case S:
                    downKey = true;
                    break;
                case E:
                    if (!hasTimeStopper)
                        break;
                    AudioController.timeStopper();
                    timeStopped = true;
                    hasTimeStopper = false;
                    itemsPicked.remove(ImageController.getItemImage(2));
                    renewInfo();
                    break;
                case SPACE:
                    if (hasPistol) {
                        AudioController.shot();
                        pistolsLeft--;
                        if (!gameGroup.getChildren().contains(playerShots[pistolsLeft]))
                            gameGroup.getChildren().add(playerShots[pistolsLeft]);
                        if (!batsSwapped)
                            setX(playerShots[pistolsLeft], xOf(player) + widthOf(player) + 2);
                        else
                            setX(playerShots[pistolsLeft], xOf(player) - widthOf(playerShots[pistolsLeft]) - 2);
                        setY(playerShots[pistolsLeft], yOf(player) + halfHeightOf(player) - halfHeightOf(playerShots[pistolsLeft]));
                        if (pistolsLeft == 0) {
                            hasPistol = false;
                            pistolsLeft = 3;
                            itemsPicked.remove(ImageController.getItemImage(9));
                            renewInfo();
                        }
                    }
                    break;
                case ESCAPE:
                    gameTimeline.stop();
                    TextController.getResultAnimation().stop();
                    stage.setScene(menuScene);
                    resetCountdown();
                    if (gameGroup.getChildren().contains(resultMessageWin))
                        gameGroup.getChildren().remove(resultMessageWin);
                    if (gameGroup.getChildren().contains(resultMessageLose))
                        gameGroup.getChildren().remove(resultMessageLose);
                    break;
            }
        });
        gameScene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case SHIFT:
                    shiftKey = false;
                    break;
                case W:
                    upKey = false;
                    break;
                case S:
                    downKey = false;
                    break;
            }
        });
    }

    public static void resetCountdown() {
        if (countdownTimeline.getStatus() == Timeline.Status.RUNNING)
            countdownTimeline.stop();
        gameGroup.getChildren().remove(countdownText);
        countdownNumber[0] = 3;
    }

    public static void resetScore() {
        missesLeft = 4;
        for (int i = 0; i < score.length; i++) {
            score[i] = ImageController.getSwitchImage(true);
        }
        ai.resetScore();
        resetObjects();
        renewInfo();
    }

    private static void resetObjects() {
        itemsPicked.clear();
        ai.resetObjects();
        ai.setHeight(BAT_HEIGHT);
        player.setHeight(BAT_HEIGHT);
        batsSwapped = false;
        playersBallTurn = true;
        magnetHighLeft = random.nextBoolean();
        magnetLowLeft = random.nextBoolean();
        timeStopped = false;
        hasTimeStopper = false;
        hasPistol = false;
        pistolsLeft = 3;
        projectileHitDuration = 0.5;
        projectileHit = false;
        blackholeActive = true;
        sinDegree = 0;
        for (Circle playerProjectile : playerShots) {
            gameGroup.getChildren().remove(playerProjectile);
        }
        for (Circle aiProjectile : aiShots)
            gameGroup.getChildren().remove(aiProjectile);
        timeStopDuration = 2.0;
        gameGroup.getChildren().remove(timeStopDurationText);
        allocator.allocateObjects();
        ballDX = 1.5;
        ballDY = 1.075;
        speedUpRatio = 1.0;
        playerSpeedScale = 1.0;
        aiSpeedScale = 1.0;
        ballLeft = false;
        ballUp = random.nextBoolean();
        ballTailIndex = 0;
        ballTailUpdate = 0;
        for (int i = 0; i < itemsInGame.length; i++)
            itemsInGame[i] = false;
        for (Item item : itemsInGroup)
            gameGroup.getChildren().remove(item);
        itemsInGroup.clear();
    }

    public static void setClassicLevel() {
        removeMagnets();
        removeBlackholes();
        gameCanvas.getGraphicsContext2D().drawImage(ImageController.getClassicSheet(), 0, 0, WIDTH, HEIGHT);
    }

    public static void setMagneticLevel() {
        removeBlackholes();
        if (!gameGroup.getChildren().contains(magnetHigh) && !gameGroup.getChildren().contains(magnetLow))
            gameGroup.getChildren().addAll(magnetHigh, magnetLow);
        gameCanvas.getGraphicsContext2D().drawImage(ImageController.getMagneticSheet(), 0, 0, WIDTH, HEIGHT);
    }

    public static void setSpaceLevel() {
        removeMagnets();
        if (!gameGroup.getChildren().contains(blackhole1) && !gameGroup.getChildren().contains(blackhole2))
            gameGroup.getChildren().addAll(blackhole1, blackhole2);
        gameCanvas.getGraphicsContext2D().drawImage(ImageController.getSpaceSheet(), 0, 0, WIDTH, HEIGHT);
    }

    private static void removeMagnets() {
        if (gameGroup.getChildren().contains(magnetHigh) && gameGroup.getChildren().contains(magnetLow)) {
            gameGroup.getChildren().remove(magnetHigh);
            gameGroup.getChildren().remove(magnetLow);
        }
    }

    private static void removeBlackholes() {
        if (gameGroup.getChildren().contains(blackhole1) && gameGroup.getChildren().contains(blackhole2)) {
            gameGroup.getChildren().remove(blackhole1);
            gameGroup.getChildren().remove(blackhole2);
        }
    }

    public static void returnToMenu() {
        stage.setScene(menuScene);
    }

    public static void main(String[] args) {
        launch(args);
        AudioController.finish();
    }

    private static class Allocator {

        void allocateObjects() {
            allocateBall();
            allocatePlayers();
            allocateAdditionalObjects();
        }

        private void allocateBall() {
            setX(ball, 20);
            setY(ball, screenCenter.getY());
            for (ImageView tailPiece : ballTail) {
                setX(tailPiece, xOf(ball));
                setY(tailPiece, yOf(ball));
            }
        }

        private void allocatePlayers() {
            for (Circle playerShot : playerShots) {
                playerShot.setFill(Color.WHITE);
                playerShot.setEffect(blur);
            }
            setY(player, screenCenter.getY() - halfHeightOf(player));
            setX(player, 2);
            setX(ai, WIDTH - 10);
            setY(ai, screenCenter.getY() - halfHeightOf(ai));
        }

        private void allocateAdditionalObjects() {
            setX(magnetHigh, WIDTH / 2 + halfWidthOf(magnetHigh));
            setY(magnetHigh, 0);
            setX(magnetLow, WIDTH / 2);
            setY(magnetLow, HEIGHT - 1);

            setX(blackhole1, screenCenter.getX() + blackholeRadius);
            setY(blackhole1, screenCenter.getY());
            setX(blackhole2, screenCenter.getX() - blackholeRadius);
            setY(blackhole2, screenCenter.getY());
        }

        private void allocateMenuObjects() {
            allocStartButton();
            allocExitButton();
            allocSoundsSwitch();
            allocMusicSwitch();
            allocItemsSwitch();
            allocDifficultyBlock();
            allocItemsBlock();
        }

        private void allocStartButton() {
            setY(startBtn, HEIGHT / 9 - halfHeightOf(startBtn));
            setX(startBtn, yOf(startBtn));
            setY(menuTexts[0], yOf(startBtn) + 50);
            setX(menuTexts[0], xOf(startBtn) + halfWidthOf(startBtn) - halfWidthOf(menuTexts[0]));
            ButtonController.initStartButton(menuTexts[0], stage, levelChooserScene);
        }

        private void allocExitButton() {
            setX(exitBtn, xOf(startBtn));
            setY(exitBtn, yOf(startBtn) + heightOf(startBtn) + 40);
            setY(menuTexts[1], yOf(exitBtn) + 50);
            setX(menuTexts[1], xOf(exitBtn) + halfWidthOf(exitBtn) - halfWidthOf(menuTexts[1]));
            ButtonController.initExitButton(menuTexts[1]);
        }

        private void allocSoundsSwitch() {
            setY(menuTexts[3], yOf(menuTexts[0]));
            setX(menuTexts[3], yOf(startBtn) + widthOf(startBtn) + 50);
            setY(soundsSwc, HEIGHT / 9 - halfHeightOf(soundsSwc));
            setX(soundsSwc, xOf(menuTexts[3]) + 20 + widthOf(menuTexts[3]));
        }

        private void allocMusicSwitch() {
            setY(menuTexts[2], yOf(menuTexts[3]) + 60);
            setX(menuTexts[2], xOf(menuTexts[3]));
            setY(musicSwc, yOf(soundsSwc) + 60);
            setX(musicSwc, xOf(soundsSwc));
        }

        private void allocItemsSwitch() {
            setY(menuTexts[4], yOf(menuTexts[2]) + 60);
            setX(menuTexts[4], xOf(menuTexts[2]));
            setY(itemsSwc, yOf(musicSwc) + 60);
            setX(itemsSwc, xOf(musicSwc));
        }

        private void allocDifficultyBlock() {
            setX(menuTexts[5], WIDTH * 0.75 - halfWidthOf(menuTexts[5]) - 28);
            setY(menuTexts[5], yOf(menuTexts[3]));

            setY(menuTexts[6], yOf(menuTexts[2]));
            setX(menuTexts[6], WIDTH / 2 + 60);
            setY(easyBtn, yOf(itemsSwc));
            setX(easyBtn, xOf(menuTexts[6]) + halfWidthOf(menuTexts[6]) - halfWidthOf(easyBtn));

            setY(menuTexts[7], yOf(menuTexts[2]));
            setX(menuTexts[7], xOf(menuTexts[6]) + widthOf(menuTexts[6]) + 40);
            setY(mediumBtn, yOf(itemsSwc));
            setX(mediumBtn, xOf(menuTexts[7]) + halfWidthOf(menuTexts[7]) - halfWidthOf(mediumBtn));

            setY(menuTexts[8], yOf(menuTexts[7]));
            setX(menuTexts[8], xOf(menuTexts[7]) + widthOf(menuTexts[7]) + 40);
            setY(hardBtn, yOf(itemsSwc));
            setX(hardBtn, xOf(menuTexts[8]) + halfWidthOf(menuTexts[8]) - halfWidthOf(hardBtn));
        }

        private void allocItemsBlock() {
            setX(menuItems[0], xOf(startBtn) + 20);
            setY(menuItems[0], yOf(startBtn) + heightOf(startBtn) + 160);
            setY(itemsTexts[0], yOf(menuItems[0]) + heightOf(itemsTexts[0]) + 10);
            setX(itemsTexts[0], xOf(menuItems[0]) + widthOf(menuItems[0]) + 20);

            setX(menuItems[5], WIDTH / 2 + 20);
            setY(menuItems[5], yOf(menuItems[0]));
            setY(itemsTexts[5], yOf(menuItems[5]) + heightOf(itemsTexts[5]) + 10);
            setX(itemsTexts[5], xOf(menuItems[5]) + widthOf(menuItems[5]) + 20);

            for (int i = 1; i < 5; i++) {
                setX(menuItems[i], xOf(menuItems[i - 1]));
                setY(menuItems[i], yOf(menuItems[i - 1]) + heightOf(menuItems[i - 1]) + 10);
                setY(itemsTexts[i], yOf(menuItems[i]) + heightOf(itemsTexts[i]) + 10);
                setX(itemsTexts[i], xOf(menuItems[i]) + widthOf(menuItems[i]) + 20);
            }
            for (int i = 6; i < 10; i++) {
                setX(menuItems[i], WIDTH / 2 + 20);
                setY(menuItems[i], yOf(menuItems[i - 5]));
                setY(itemsTexts[i], yOf(menuItems[i]) + heightOf(itemsTexts[i]) + 10);
                setX(itemsTexts[i], xOf(menuItems[i]) + widthOf(menuItems[i]) + 20);
            }

            setX(itemsBackground, xOf(menuItems[0]) - 20);
            setY(itemsBackground, yOf(menuItems[0]) - 15);
        }

        void allocateLevelButtons() {
            setX(classicLevelBtn, 190);
            setY(classicLevelBtn, 200);
            setX(levelTexts[0], xOf(classicLevelBtn) + halfWidthOf(classicLevelBtn) - halfWidthOf(levelTexts[0]));
            setY(levelTexts[0], yOf(classicLevelBtn) + 50);
            ButtonController.initClassicLevel(levelTexts[0], stage, gameScene, gameTimeline, countdownTimeline);

            setX(magneticLevelBtn, xOf(classicLevelBtn) + widthOf(classicLevelBtn) + 70);
            setY(magneticLevelBtn, yOf(classicLevelBtn));
            setX(levelTexts[1], xOf(magneticLevelBtn) + halfWidthOf(magneticLevelBtn) - halfWidthOf(levelTexts[1]));
            setY(levelTexts[1], yOf(magneticLevelBtn) + 50);
            ButtonController.initMagneticLevel(levelTexts[1], stage, gameScene, gameTimeline, countdownTimeline);

            setX(spaceLevelBtn, xOf(magneticLevelBtn) + widthOf(magneticLevelBtn) + 70);
            setY(spaceLevelBtn, yOf(classicLevelBtn));
            setX(levelTexts[2], xOf(spaceLevelBtn) + halfWidthOf(spaceLevelBtn) - halfWidthOf(levelTexts[2]));
            setY(levelTexts[2], yOf(spaceLevelBtn) + 50);
            ButtonController.initSpaceLevel(levelTexts[2], stage, gameScene, gameTimeline, countdownTimeline);

            setX(returnBtn, xOf(classicLevelBtn));
            setY(returnBtn, HEIGHT - heightOf(returnBtn) - 100);
            setX(levelTexts[3], xOf(returnBtn) + halfWidthOf(returnBtn) - halfWidthOf(levelTexts[3]));
            setY(levelTexts[3], yOf(returnBtn) + 50);
            ButtonController.initReturnButton(levelTexts[3]);

            setX(levelTexts[4], 190);
            setY(levelTexts[4], screenCenter.getY());

            setX(levelTexts[5], 250);
            setY(levelTexts[5], screenCenter.getY() + 60);

            setX(levelTexts[6], 160);
            setY(levelTexts[6], screenCenter.getY() + 120);
        }
    }

    public static class ItemHandler {

        public static void swapBats() {
            AudioController.itemPicked();
            batsSwapped = !batsSwapped;
            double aiX = xOf(ai), playerX = xOf(player);
            setX(player, aiX / 2);
            setX(ai, playerX);
            setX(player, aiX);
            renewInfo();
        }

        public static void batResize(boolean increase) {
            AudioController.itemPicked();
            Rectangle whoPicked = playersBallTurn ? player : ai;
            if (increase) {
                setY(whoPicked, (yOf(whoPicked) + heightOf(whoPicked) + 10 >= HEIGHT - 2) ? yOf(whoPicked) - 10 : yOf(whoPicked) - 5);
                whoPicked.setHeight(heightOf(whoPicked) + 10);
            } else {
                if (heightOf(whoPicked) - 10 <= 5) {
                    whoPicked.setHeight(5);
                    renewInfo();
                    return;
                }
                whoPicked.setHeight(heightOf(whoPicked) - 10);
                setY(whoPicked, yOf(whoPicked) + 5);
            }
            if (whoPicked == player) {
                if (!itemsPicked.contains(ImageController.getItemImage(increase ? 0 : 1)))
                    itemsPicked.add(ImageController.getItemImage(increase ? 0 : 1));
            } else {
                if (!ai.getItemsPicked().contains(ImageController.getItemImage(increase ? 0 : 1)))
                    ai.getItemsPicked().add(ImageController.getItemImage(increase ? 0 : 1));
            }
            renewInfo();
        }

        public static void setBallSpeedUpScale(double scale) {
            AudioController.itemPicked();
            speedUpRatio *= scale;
            ballDX *= scale;
        }

        public static void changeBatSpeed(boolean speedUp) {
            AudioController.itemPicked();
            if (speedUp) {
                if (playersBallTurn) {
                    playerSpeedScale *= 1.16;
                    if (!itemsPicked.contains(ImageController.getItemImage(5)))
                        itemsPicked.add(ImageController.getItemImage(5));
                } else {
                    aiSpeedScale *= 1.16;
                    if (!ai.getItemsPicked().contains(ImageController.getItemImage(5)))
                        ai.getItemsPicked().add(ImageController.getItemImage(5));
                }
            } else {
                if (playersBallTurn) {
                    playerSpeedScale *= 0.84;
                    if (!itemsPicked.contains(ImageController.getItemImage(6)))
                        itemsPicked.add(ImageController.getItemImage(6));
                } else {
                    aiSpeedScale *= 0.84;
                    if (!ai.getItemsPicked().contains(ImageController.getItemImage(6)))
                        ai.getItemsPicked().add(ImageController.getItemImage(6));
                }
            }
            renewInfo();
        }

        public static void timeStop() {
            AudioController.itemPicked();
            if (playersBallTurn) {
                hasTimeStopper = true;
                if (!itemsPicked.contains(ImageController.getItemImage(2)))
                    itemsPicked.add(ImageController.getItemImage(2));
            } else {
                ai.setTimeStopper(true);
                if (!ai.getItemsPicked().contains(ImageController.getItemImage(2)))
                    ai.getItemsPicked().add(ImageController.getItemImage(2));
            }
            renewInfo();
        }

        public static void plusScore() {
            AudioController.itemPicked();
            if (playersBallTurn) {
                if (missesLeft == 4)
                    return;
                missesLeft++;
                score[missesLeft] = ImageController.getSwitchImage(true);
                renewInfo();
            } else {
                if (ai.getMissesLeft() == 4)
                    return;
                ai.setMissesLeft(ai.getMissesLeft() + 1);
                ai.getScore()[ai.getMissesLeft()] = ImageController.getSwitchImage(true);
                renewInfo();
            }
        }

        public static void addPistol() {
            AudioController.pistolPicked();
            if (playersBallTurn) {
                hasPistol = true;
                pistolsLeft = 3;
                if (!itemsPicked.contains(ImageController.getItemImage(9)))
                    itemsPicked.add(ImageController.getItemImage(9));
            } else {
                ai.setHasPistol(true);
                ai.setShotsLeft(3);
                if (!ai.getItemsPicked().contains(ImageController.getItemImage(9)))
                    ai.getItemsPicked().add(ImageController.getItemImage(9));
            }
            renewInfo();
        }
    }
}
