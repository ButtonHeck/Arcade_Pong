package com.buttonHeck.pong;

import com.buttonHeck.pong.handler.AudioHandler;
import com.buttonHeck.pong.handler.ButtonHandler;
import com.buttonHeck.pong.handler.ImageHandler;
import com.buttonHeck.pong.handler.TextHandler;
import com.buttonHeck.pong.item.Item;
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
    public static final double DEFAULT_PADDLE_HEIGHT = HEIGHT / 8;
    private static final double BALL_REFLECT_RATIO = 38.5;
    private static Stage stage;
    private static boolean upKey, downKey, shiftKey;
    private static Allocator allocator;
    private static Random random;
    private static double updateSpeed = 0.0018;
    private static Text resultMessageWin, resultMessageLose;
    private static Timeline countdownTimeline;
    private static Text countdownText;
    private static final int countdownOrdinal[] = {3};
    private static Timeline countdownTextTimeline;
    private static final int countdownFontSize[] = {96 * 4};
    private static Point2D screenCenter;

    static {
        resultMessageWin = TextHandler.getResultMessage(true);
        resultMessageLose = TextHandler.getResultMessage(false);
        countdownText = TextHandler.getCountdown();
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
    private static Text[] menuTexts = TextHandler.getButtonsTexts();
    private static Text[] itemsTexts = TextHandler.getItemsTexts();
    private static Text author = TextHandler.getAuthorText();

    //Game scene variables
    static Scene gameScene;
    private static VBox gameRoot = new VBox();
    private static Group gamePanel = new Group();
    private static Canvas gameCanvas, infoCanvas;
    private static double winRateCoords[] = new double[22];
    private static Timeline gameTimeline = new Timeline();
    private static final GaussianBlur BLUR = new GaussianBlur(2);
    private static ImageView ball;
    private static ImageView[] ballTail;
    private static int ballTailIndex, ballTailFrameUpdate;
    private static ImageView magnetHigh, magnetLow;
    private static ImageView blackhole1, blackhole2;

    //Game logic variables
    private static boolean ballLeft, ballUp;
    private static double ballDX = 1.5, ballDY = 1.075;
    private static double ballSpeedRatio = 1.0; //changes when ball speed up/down item is picked
    private static boolean batsSwapped;
    private static boolean playersBallTurn = true;
    private static boolean magnetHighLeft = true, magnetLowLeft = false;
    private static boolean[] itemsPresentInGame = new boolean[10];
    private static ArrayList<Item> items = new ArrayList<>();
    private static double playerSpeedScale, aiSpeedScale;
    //used in space level
    private static double blackholeRadius = 210;
    private static double sinDegree = 0;
    private static boolean blackholeActive = true;

    //Level chooser scene
    private static Scene levelChooserScene;
    private static Text[] levelTexts = TextHandler.getLevelTexts();
    private static ImageView classicLevelBtn, magneticLevelBtn, spaceLevelBtn, returnBtn;

    //Player variables
    private static Rectangle player;
    private static double playerBasicSpeed = ballDY * 1.2;
    private static int missesLeft;
    private static Image score[] = new Image[]{
            ImageHandler.getSwitchImage(true),
            ImageHandler.getSwitchImage(true),
            ImageHandler.getSwitchImage(true),
            ImageHandler.getSwitchImage(true),
            ImageHandler.getSwitchImage(true)
    };
    private static ArrayList<Image> itemsPicked;
    private static boolean hasTimeStopper, timeStopped;
    private static double timeStopDuration = 2;
    private static Text timeStopDurationText = TextHandler.getTimeStopDurationText();
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
        initializeObjects();
        initializeGameCanvases();
        allocator.allocateObjects();
        buildGameContentTree();
        initializeTimelines();
        allocator.allocateMenuObjects();
        allocator.allocateLevelButtons();
        buildMenuContentTree();
        resetCountdown();
        globalReset();
        launchGame();
        initializeKeyboardHandler();
        AudioHandler.playMusic();
    }

    private void initializeObjects() {
        initializePlayerPaddle();
        initializeAiPaddle();
        initializeBall();
        initializeAdditionalObjects();
        for (int i = 0; i < menuItems.length; i++)
            menuItems[i] = ButtonHandler.getItem(i);
        initializeMenuButtons();
        initializeLevelChooseScene();
    }

    private void initializePlayerPaddle() {
        player = new Rectangle(8, DEFAULT_PADDLE_HEIGHT);
        player.setFill(Color.LIGHTBLUE);
        player.setEffect(BLUR);
        itemsPicked = new ArrayList<>();
    }

    private void initializeAiPaddle() {
        ai = new AI(8, DEFAULT_PADDLE_HEIGHT);
        aiShots = ai.getAiShots();
    }

    private void initializeBall() {
        ball = new ImageView(ImageHandler.getBallImage());
        ballTail = new ImageView[]{
                new ImageView(ImageHandler.getBallImage()),
                new ImageView(ImageHandler.getBallImage()),
                new ImageView(ImageHandler.getBallImage()),
                new ImageView(ImageHandler.getBallImage()),
                new ImageView(ImageHandler.getBallImage())
        };
        ballTailIndex = 0;
        ballTailFrameUpdate = 0;
        for (ImageView tailPiece : ballTail)
            tailPiece.setOpacity(0.1);
    }

    private void initializeAdditionalObjects() {
        magnetHigh = new ImageView(ImageHandler.getMagnetImage());
        magnetLow = new ImageView(ImageHandler.getMagnetImage());
        magnetLow.getTransforms().add(new Rotate(180));
        blackhole1 = new ImageView(ImageHandler.getBlackholeImage());
        blackhole1.setEffect(BLUR);
        blackhole2 = new ImageView(ImageHandler.getBlackholeImage());
        blackhole2.setEffect(BLUR);
    }

    private void initializeMenuButtons() {
        startBtn = ButtonHandler.getStartButton();
        exitBtn = ButtonHandler.getExitButton();
        musicSwc = ButtonHandler.getMusicSwitch();
        soundsSwc = ButtonHandler.getSoundsSwitch();
        itemsSwc = ButtonHandler.getItemsSwitch();
        easyBtn = ButtonHandler.getEasyButton();
        mediumBtn = ButtonHandler.getMediumButton();
        hardBtn = ButtonHandler.getHardButton();
    }

    private void initializeLevelChooseScene() {
        classicLevelBtn = ButtonHandler.getClassicLevelBtn();
        magneticLevelBtn = ButtonHandler.getMagneticLevelBtn();
        spaceLevelBtn = ButtonHandler.getSpaceLevelBtn();
        returnBtn = ButtonHandler.getReturnBtn();
        Group levelRoot = new Group();
        levelRoot.getChildren().addAll(classicLevelBtn, magneticLevelBtn, spaceLevelBtn, returnBtn);
        for (Text text : levelTexts)
            levelRoot.getChildren().add(text);
        levelChooserScene = new Scene(levelRoot, WIDTH, HEIGHT, Color.BLACK);
    }

    private void initializeGameCanvases() {
        initializeGameCanvas();
        initializeMenuItemsCanvas();
        initializeInfoCanvas();
        renewWinratePanel();
    }

    private void initializeGameCanvas() {
        gameCanvas = new Canvas(WIDTH * SIZE_SCALE, HEIGHT * SIZE_SCALE);
        gameCanvas.getGraphicsContext2D().setFill(Color.BLACK);
        gameCanvas.getGraphicsContext2D().fillRect(0, 0, widthOf(gameCanvas), heightOf(gameCanvas));
    }

    private void initializeMenuItemsCanvas() {
        itemsBackground.setOpacity(0.3);
        itemsBackground.getGraphicsContext2D().setFill(Color.DARKSLATEBLUE);
        itemsBackground.getGraphicsContext2D().fillRect(0, 0, widthOf(itemsBackground), heightOf(itemsBackground));
        itemsBackground.setEffect(new GaussianBlur(20));
    }

    private void initializeInfoCanvas() {
        infoCanvas = new Canvas(WIDTH * SIZE_SCALE, HEIGHT * SIZE_SCALE / 15);
        infoCanvas.getGraphicsContext2D().setFill(Color.BLACK.brighter());
        infoCanvas.getGraphicsContext2D().fillRect(0, 0, infoCanvas.getWidth(), infoCanvas.getHeight());
    }

    static void renewWinratePanel() {
        renewWinrateCoordinates();
        renewWinrateIndicators();
    }

    private static void renewWinrateCoordinates() {
        if (!batsSwapped) {
            for (int i = 0; i < 11; i++)
                winRateCoords[i] = 10 + i * 45;
            for (int i = 11; i < 22; i++)
                winRateCoords[i] = widthOf(infoCanvas) - 50 - i * 45;
        } else {
            for (int i = 0; i < 11; i++)
                winRateCoords[i] = widthOf(infoCanvas) - 50 - i * 45;
            for (int i = 11; i < 22; i++)
                winRateCoords[i] = 10 + i * 45;
        }
    }

    private static void renewWinrateIndicators() {
        infoCanvas.getGraphicsContext2D().setFill(Color.BLACK.brighter());
        infoCanvas.getGraphicsContext2D().fillRect(235, 0, 512 + 45, infoCanvas.getHeight());
        for (int i = 0; i < itemsPicked.size(); i++)
            infoCanvas.getGraphicsContext2D().drawImage(itemsPicked.get(i), winRateCoords[i + 5], 5);
        for (int i = 0; i < ai.getItemsPicked().size(); i++)
            infoCanvas.getGraphicsContext2D().drawImage(ai.getItemsPicked().get(i), winRateCoords[i + 16], 5);
        for (int i = 0; i < 5; i++)
            infoCanvas.getGraphicsContext2D().drawImage(score[i], winRateCoords[i], 5);
        for (int i = 0; i < 5; i++)
            infoCanvas.getGraphicsContext2D().drawImage(ai.getScore()[i], winRateCoords[i + 11], 5);
    }

    private void buildGameContentTree() {
        gamePanel.getChildren().addAll(gameCanvas, ball, player, ai);
        gamePanel.getChildren().addAll(ballTail);
        gameRoot.getChildren().add(gamePanel);
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
            tickFrame();
            if (gamePanel.getChildren().contains(magnetHigh))
                magnetEvent();
            if (gamePanel.getChildren().contains(blackhole1))
                blackholeEvent();
        });
        gameTimeline.getKeyFrames().add(gameFrame);
        gameTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    private boolean aiTimeStopEvent() {
        if (ai.isTimeStopRequested()) {
            ai.setTimeStopper(false);
            ai.getItemsPicked().remove(ImageHandler.getItemImage(2));
            renewWinratePanel();
            if (!gamePanel.getChildren().contains(timeStopDurationText))
                timeStopActivate();
            ai.move(ball, aiActualSpeed, aiBasicSpeed * aiSpeedScale, ballDY);
            ai.setTimeStopDuration(ai.getTimeStopDuration() - updateSpeed);
            timeStopDurationText.setText(String.valueOf(ai.getTimeStopDuration()).substring(0, 4));
            timeStopDurationText.setOpacity(ai.getTimeStopDuration() / 2);
            if (ai.getTimeStopDuration() <= 0) {
                ai.disableTimeStopRequest();
                ai.setTimeStopDuration(2.0);
                gamePanel.getChildren().remove(timeStopDurationText);
                timeStopDurationText.setText(String.valueOf(2.0));
            }
            return true;
        }
        return false;
    }

    private void buildMenuContentTree() {
        menuRoot = new Group();
        menuRoot.getChildren().addAll(startBtn, exitBtn, musicSwc, soundsSwc, itemsSwc,
                easyBtn, mediumBtn, hardBtn, itemsBackground, author);
        menuRoot.getChildren().addAll(menuItems);
        menuRoot.getChildren().addAll(itemsTexts);
        menuRoot.getChildren().addAll(menuTexts);
    }

    private void timeStopActivate() {
        AudioHandler.timeStopper();
        setX(timeStopDurationText, xOf(ball) - 15);
        setY(timeStopDurationText, (yOf(ball) >= WIDTH / 2 ? yOf(ball) - 15 : yOf(ball) + heightOf(ball) + 15));
        gamePanel.getChildren().add(timeStopDurationText);
    }

    private void playerMoveEvent() {
        if (!projectileHit)
            movePlayer();
        else
            tickPlayerStun();
    }

    private void movePlayer() {
        if (upKey) {
            if (player.getBoundsInParent().getMinY() >= playerBasicSpeed * playerSpeedScale)
                setY(player, yOf(player) - playerBasicSpeed * playerSpeedScale * (shiftKey ? 0.5 : 1.0));
        } else if (downKey) {
            if (player.getBoundsInParent().getMaxY() <= HEIGHT - playerBasicSpeed * playerSpeedScale)
                setY(player, yOf(player) + playerBasicSpeed * playerSpeedScale * (shiftKey ? 0.5 : 1.0));
        }
    }

    private void tickPlayerStun() {
        projectileHitDuration -= updateSpeed;
        if (projectileHitDuration <= 0) {
            projectileHit = false;
            projectileHitDuration = 0.5;
        }
    }

    private void tickFrame() {
        if (!timeStopped) {
            moveBallTail();
            if (gamePanel.getChildren().contains(magnetHigh))
                checkBallMagnet();
            if (gamePanel.getChildren().contains(blackhole1))
                checkBallBlackhole();
            checkBallBatsCollision();
            checkBallScreenCollision();
            checkBallItemCollision();
            if (ai.hasPistol() && ai.isShootRequest()) {
                if (ai.getShotsLeft() <= 0) {
                    ai.resetPistol();
                    renewWinratePanel();
                } else {
                    ai.setShotsLeft(ai.getShotsLeft() - 1);
                    AudioHandler.shot();
                }
                if (ai.getShotsLeft() < 3) {
                    if (!batsSwapped)
                        setX(ai.getAiShots()[ai.getShotsLeft()], xOf(ai) - widthOf(ai.getAiShots()[ai.getShotsLeft()]) - 2);
                    else
                        setX(ai.getAiShots()[ai.getShotsLeft()], xOf(ai) + widthOf(ai) + 2);
                    setY(ai.getAiShots()[ai.getShotsLeft()], yOf(ai) + halfHeightOf(ai) - halfHeightOf(ai.getAiShots()[ai.getShotsLeft()]));
                    gamePanel.getChildren().add(ai.getAiShots()[ai.getShotsLeft()]);
                }
            }
            movePlayerProjectiles();
            moveAIProjectiles();
            if (Options.isItemsOn())
                rollSpawnItem();
            if (ai.isConfusedYet(updateSpeed))
                return;
            ai.move(ball, aiActualSpeed, aiBasicSpeed * aiSpeedScale, ballDY);
        } else {
            if (!gamePanel.getChildren().contains(timeStopDurationText))
                timeStopActivate();
            tickTimeStopDuration();
        }
    }

    private void moveBallTail() {
        if (++ballTailFrameUpdate == 8) {
            setXY(ballTail[Math.abs(ballTailIndex) % ballTail.length], xOf(ball), yOf(ball));
            ballTailIndex += 1;
            ballTailFrameUpdate = 0;
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
            AudioHandler.blackhole();
            setXY(ball, xOf(blackhole2) + halfWidthOf(blackhole2), yOf(blackhole2) + halfHeightOf(blackhole2));
        }
        if (ball.getBoundsInParent().intersects(blackhole2.getBoundsInParent()) && blackholeActive) {
            blackholeActive = false;
            AudioHandler.blackhole();
            setXY(ball, xOf(blackhole1) + halfWidthOf(blackhole1), yOf(blackhole1) + halfHeightOf(blackhole1));
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
        ballDY = (yOf(bat) + halfHeightOf(bat) - (yOf(ball) + halfHeightOf(ball))) / BALL_REFLECT_RATIO * ballSpeedRatio;
        ballUp = ballDY < 0;
        ballDY = Math.abs(ballDY);
        ballLeft = !ballLeft;
        ballDX += 0.0022;
        blackholeActive = true;
        playersBallTurn = batsSwapped;
        AudioHandler.batReflection();
    }

    private void checkBallScreenCollision() {
        setX(ball, xOf(ball) + (ballLeft ? -ballDX : ballDX));
        if (xOf(ball) <= ballDX + 1) {
            ballLeft = false;
            AudioHandler.screenReflection();
            if (!batsSwapped)
                playerMissed();
            else
                aiMissed();
        }
        if (xOf(ball) + widthOf(ball) >= WIDTH - 3) {
            ballLeft = true;
            AudioHandler.screenReflection();
            if (!batsSwapped)
                aiMissed();
            else
                playerMissed();
        }

        setY(ball, yOf(ball) + (ballUp ? -ballDY : ballDY));
        if (yOf(ball) <= ballDY + 1) {
            AudioHandler.screenReflection();
            ballUp = false;
        }
        if (yOf(ball) + heightOf(ball) + ballDY >= HEIGHT - 3) {
            AudioHandler.screenReflection();
            ballUp = true;
        }
    }

    private void playerMissed() {
        score[missesLeft] = ImageHandler.getSwitchImage(false);
        renewWinratePanel();
        if (missesLeft == 0) {
            if (!gamePanel.getChildren().contains(resultMessageLose))
                gamePanel.getChildren().add(resultMessageLose);
            gameTimeline.stop();
            AudioHandler.result();
            TextHandler.getResultLightAnimation().play();
            return;
        }
        missesLeft--;
    }

    private void aiMissed() {
        ai.ballMissed();
        renewWinratePanel();
        if (ai.getMissesLeft() == 0) {
            if (!gamePanel.getChildren().contains(resultMessageWin))
                gamePanel.getChildren().add(resultMessageWin);
            gameTimeline.stop();
            AudioHandler.result();
            TextHandler.getResultLightAnimation().play();
            return;
        }
        ai.decreaseMissesLeft();
    }

    private void checkBallItemCollision() {
        Iterator<Item> itemsIterator = items.iterator();
        while (itemsIterator.hasNext()) {
            Item currentItem = itemsIterator.next();
            if (ball.getBoundsInParent().intersects(currentItem.getBoundsInParent())) {
                currentItem.applyAction();
                itemsPresentInGame[currentItem.ordinal] = false;
                gamePanel.getChildren().remove(currentItem);
                itemsIterator.remove();
                break;
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
            if (gamePanel.getChildren().contains(playerShot)) {
                if (playerShot.getBoundsInParent().intersects(ai.getBoundsInParent())) {
                    ai.setProjectileHit(true);
                    gamePanel.getChildren().remove(playerShot);
                    return;
                }
                if (!batsSwapped)
                    tickPlayerProjectileMoveRight(playerShot);
                else
                    tickPlayerProjectileMoveLeft(playerShot);
            }
        }
    }

    private void tickPlayerProjectileMoveRight(Circle playerShot) {
        if (xOf(playerShot) + 1.5 >= WIDTH - 1.5)
            gamePanel.getChildren().remove(playerShot);
        else
            setX(playerShot, xOf(playerShot) + 1.5);
    }

    private void tickPlayerProjectileMoveLeft(Circle playerShot) {
        if (xOf(playerShot) <= 5)
            gamePanel.getChildren().remove(playerShot);
        else
            setX(playerShot, xOf(playerShot) - 1.5);
    }

    private void moveAIProjectiles() {
        for (Circle aiShot : aiShots) {
            ai.setShootRequest(false);
            if (gamePanel.getChildren().contains(aiShot)) {
                if (aiShot.getBoundsInParent().intersects(player.getBoundsInParent())) {
                    projectileHit = true;
                    gamePanel.getChildren().remove(aiShot);
                    return;
                }
                if (!batsSwapped)
                    tickAiProjectileMoveLeft(aiShot);
                else
                    tickAiProjectileMoveRight(aiShot);
            }
        }
    }

    private void tickAiProjectileMoveLeft(Circle aiShot) {
        if (xOf(aiShot) <= 5) {
            gamePanel.getChildren().remove(aiShot);
            ai.setShootRequest(true);
        } else
            setX(aiShot, xOf(aiShot) - 1.5);
    }

    private void tickAiProjectileMoveRight(Circle aiShot) {
        if (xOf(aiShot) + 1.5 >= WIDTH - 1.5) {
            gamePanel.getChildren().remove(aiShot);
            ai.setShootRequest(true);
        } else
            setX(aiShot, xOf(aiShot) + 1.5);
    }

    private void rollSpawnItem() {
        if (random.nextInt(1600) == 11) {
            int ordinal = random.nextInt(10);
            if (itemsPresentInGame[ordinal])
                return;
            Item item = Item.getItemByOrdinal(ordinal).copy();
            item.setTranslateX(WIDTH / 2 + Math.random() * 700 - 350);
            item.setTranslateY(HEIGHT / 2 + Math.random() * 600 - 300);
            for (int i = 0; i < gamePanel.getChildren().size(); i++) {
                if (Math.abs(xOf(item) - xOf(gamePanel.getChildren().get(i))) <= widthOf(item)
                        || Math.abs(yOf(item) - yOf(gamePanel.getChildren().get(i))) <= heightOf(item))
                    return;
            }
            items.add(item);
            gamePanel.getChildren().add(item);
            itemsPresentInGame[ordinal] = true;
        }
    }

    private void tickTimeStopDuration() {
        timeStopDuration -= updateSpeed;
        timeStopDurationText.setText(String.valueOf(timeStopDuration).substring(0, 4));
        timeStopDurationText.setOpacity(timeStopDuration / 2);
        if (timeStopDuration <= 0) {
            timeStopped = false;
            timeStopDuration = 2;
            gamePanel.getChildren().remove(timeStopDurationText);
            timeStopDurationText.setText(String.valueOf(2.0));
        }
    }

    private void initCountdownTimeline() {
        countdownTimeline.getKeyFrames().add(initializeCountdownFrame());
        countdownTimeline.setCycleCount(4);
        countdownTextTimeline.getKeyFrames().add(initializeCountdownTextFrame());
        countdownTextTimeline.setCycleCount(Animation.INDEFINITE);
    }

    private KeyFrame initializeCountdownFrame() {
        return new KeyFrame(Duration.seconds(1), event -> {
            if (!gamePanel.getChildren().contains(countdownText))
                gamePanel.getChildren().add(countdownText);
            AudioHandler.countdown();
            countdownText.setText(String.valueOf(countdownOrdinal[0]--));
            countdownTextTimeline.play();
            setXY(countdownText, 0, 0);
            countdownFontSize[0] = 96 * 4;
        });
    }

    private KeyFrame initializeCountdownTextFrame() {
        return new KeyFrame(Duration.millis(10), event -> {
            countdownFontSize[0] -= 2;
            if (countdownFontSize[0] < 192)
                countdownFontSize[0] = 192;
            countdownText.setFont(new Font("Arial", countdownFontSize[0]));
            setXY(countdownText, xOf(countdownText) + 0.72, yOf(countdownText) - 0.72);
        });
    }

    private void launchGame() {
        menuScene = new Scene(menuRoot, WIDTH, HEIGHT, Color.BLACK);
        stage.setScene(menuScene);
        stage.setTitle("Arcade pong");
        stage.show();
        stage.setResizable(false);
    }

    private void initializeKeyboardHandler() {
        initializeKeyPressedHandler();
        initializeKeyReleasedHandler();
    }

    private void initializeKeyPressedHandler() {
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
                    timeStopped = true;
                    hasTimeStopper = false;
                    itemsPicked.remove(ImageHandler.getItemImage(2));
                    renewWinratePanel();
                    break;
                case SPACE:
                    if (hasPistol)
                        spawnPlayerProjectile();
                    break;
                case ESCAPE:
                    returnToMenu();
                    break;
            }
        });
    }

    private void initializeKeyReleasedHandler() {
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

    private void spawnPlayerProjectile() {
        AudioHandler.shot();
        --pistolsLeft;
        if (!gamePanel.getChildren().contains(playerShots[pistolsLeft]))
            gamePanel.getChildren().add(playerShots[pistolsLeft]);
        if (!batsSwapped)
            setX(playerShots[pistolsLeft], xOf(player) + widthOf(player) + 2);
        else
            setX(playerShots[pistolsLeft], xOf(player) - widthOf(playerShots[pistolsLeft]) - 2);
        setY(playerShots[pistolsLeft], yOf(player) + halfHeightOf(player) - halfHeightOf(playerShots[pistolsLeft]));
        if (pistolsLeft == 0) {
            hasPistol = false;
            pistolsLeft = 3;
            itemsPicked.remove(ImageHandler.getItemImage(9));
            renewWinratePanel();
        }
    }

    public static void resetCountdown() {
        if (countdownTimeline.getStatus() == Timeline.Status.RUNNING)
            countdownTimeline.stop();
        gamePanel.getChildren().remove(countdownText);
        countdownOrdinal[0] = 3;
    }

    public static void globalReset() {
        resetScore();
        resetObjects();
        renewWinratePanel();
    }

    private static void resetScore() {
        missesLeft = 4;
        for (int i = 0; i < score.length; i++)
            score[i] = ImageHandler.getSwitchImage(true);
        ai.resetScore();
    }

    private static void resetObjects() {
        itemsPicked.clear();
        ai.resetObjects();
        resetPlayerLogic();
        resetBallLogic();
        batsSwapped = false;
        timeStopDuration = 2.0;
        aiSpeedScale = 1.0;
        gamePanel.getChildren().removeAll(playerShots);
        gamePanel.getChildren().removeAll(aiShots);
        gamePanel.getChildren().remove(timeStopDurationText);
        allocator.allocateObjects();
        for (int i = 0; i < itemsPresentInGame.length; i++)
            itemsPresentInGame[i] = false;
        gamePanel.getChildren().removeAll(items);
        items.clear();
    }

    private static void resetPlayerLogic() {
        player.setHeight(DEFAULT_PADDLE_HEIGHT);
        playersBallTurn = true;
        timeStopped = false;
        hasTimeStopper = false;
        hasPistol = false;
        pistolsLeft = 3;
        projectileHitDuration = 0.5;
        projectileHit = false;
        playerSpeedScale = 1.0;
    }

    private static void resetBallLogic() {
        ballDX = 1.5;
        ballDY = 1.075;
        ballSpeedRatio = 1.0;
        ballLeft = false;
        ballUp = random.nextBoolean();
        ballTailIndex = 0;
        ballTailFrameUpdate = 0;
    }

    public static void setClassicLevel() {
        gamePanel.getChildren().removeAll(magnetHigh, magnetLow, blackhole1, blackhole2);
        gameCanvas.getGraphicsContext2D().drawImage(ImageHandler.getClassicSheet(), 0, 0, WIDTH, HEIGHT);
    }

    public static void setMagneticLevel() {
        gamePanel.getChildren().removeAll(blackhole1, blackhole2);
        if (!gamePanel.getChildren().contains(magnetHigh) && !gamePanel.getChildren().contains(magnetLow))
            gamePanel.getChildren().addAll(magnetHigh, magnetLow);
        gameCanvas.getGraphicsContext2D().drawImage(ImageHandler.getMagneticSheet(), 0, 0, WIDTH, HEIGHT);
        magnetHighLeft = random.nextBoolean();
        magnetLowLeft = random.nextBoolean();
    }

    public static void setSpaceLevel() {
        gamePanel.getChildren().removeAll(magnetHigh, magnetLow);
        if (!gamePanel.getChildren().contains(blackhole1) && !gamePanel.getChildren().contains(blackhole2))
            gamePanel.getChildren().addAll(blackhole1, blackhole2);
        gameCanvas.getGraphicsContext2D().drawImage(ImageHandler.getSpaceSheet(), 0, 0, WIDTH, HEIGHT);
        blackholeActive = true;
        sinDegree = 0;
    }

    public static void returnToMenu() {
        gameTimeline.stop();
        TextHandler.getResultLightAnimation().stop();
        stage.setScene(menuScene);
        resetCountdown();
        gamePanel.getChildren().removeAll(resultMessageWin, resultMessageLose);
    }

    public static void main(String[] args) {
        launch(args);
        AudioHandler.finish();
    }

    private static class Allocator {

        void allocateObjects() {
            allocateBall();
            allocatePlayers();
            allocateAdditionalObjects();
        }

        private void allocateBall() {
            setXY(ball, 20, screenCenter.getY());
            for (ImageView tailPiece : ballTail)
                setXY(tailPiece, xOf(ball), yOf(ball));
        }

        private void allocatePlayers() {
            setXY(player, 2, screenCenter.getY() - halfHeightOf(player));
            setXY(ai, WIDTH - 10, screenCenter.getY() - halfHeightOf(ai));
            for (Circle playerShot : playerShots) {
                playerShot.setFill(Color.WHITE);
                playerShot.setEffect(BLUR);
            }
        }

        private void allocateAdditionalObjects() {
            setXY(magnetHigh, WIDTH / 2 + halfWidthOf(magnetHigh), 0);
            setXY(magnetLow, WIDTH / 2, HEIGHT - 1);
            setXY(blackhole1, screenCenter.getX() + blackholeRadius, screenCenter.getY());
            setXY(blackhole2, screenCenter.getX() - blackholeRadius, screenCenter.getY());
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
            setXY(menuTexts[0], xOf(startBtn) + halfWidthOf(startBtn) - halfWidthOf(menuTexts[0]), yOf(startBtn) + 50);
            ButtonHandler.initStartButton(menuTexts[0], stage, levelChooserScene);
        }

        private void allocExitButton() {
            setXY(exitBtn, xOf(startBtn), yOf(startBtn) + heightOf(startBtn) + 40);
            setXY(menuTexts[1], xOf(exitBtn) + halfWidthOf(exitBtn) - halfWidthOf(menuTexts[1]), yOf(exitBtn) + 50);
            ButtonHandler.initExitButton(menuTexts[1]);
        }

        private void allocSoundsSwitch() {
            setXY(menuTexts[3], yOf(startBtn) + widthOf(startBtn) + 50, yOf(menuTexts[0]));
            setXY(soundsSwc, xOf(menuTexts[3]) + 20 + widthOf(menuTexts[3]), HEIGHT / 9 - halfHeightOf(soundsSwc));
        }

        private void allocMusicSwitch() {
            setXY(menuTexts[2], xOf(menuTexts[3]), yOf(menuTexts[3]) + 60);
            setXY(musicSwc, xOf(soundsSwc), yOf(soundsSwc) + 60);
        }

        private void allocItemsSwitch() {
            setXY(menuTexts[4], xOf(menuTexts[2]), yOf(menuTexts[2]) + 60);
            setXY(itemsSwc, xOf(musicSwc), yOf(musicSwc) + 60);
        }

        private void allocDifficultyBlock() {
            setXY(menuTexts[5], WIDTH * 0.75 - halfWidthOf(menuTexts[5]) - 28, yOf(menuTexts[3]));
            setXY(menuTexts[6], WIDTH / 2 + 60, yOf(menuTexts[2]));
            setXY(easyBtn, xOf(menuTexts[6]) + halfWidthOf(menuTexts[6]) - halfWidthOf(easyBtn), yOf(itemsSwc));
            setXY(menuTexts[7], xOf(menuTexts[6]) + widthOf(menuTexts[6]) + 40, yOf(menuTexts[2]));
            setXY(mediumBtn, xOf(menuTexts[7]) + halfWidthOf(menuTexts[7]) - halfWidthOf(mediumBtn), yOf(itemsSwc));
            setXY(menuTexts[8], xOf(menuTexts[7]) + widthOf(menuTexts[7]) + 40, yOf(menuTexts[7]));
            setXY(hardBtn, xOf(menuTexts[8]) + halfWidthOf(menuTexts[8]) - halfWidthOf(hardBtn), yOf(itemsSwc));
        }

        private void allocItemsBlock() {
            setXY(menuItems[0], xOf(startBtn) + 20, yOf(startBtn) + heightOf(startBtn) + 160);
            setXY(itemsTexts[0], xOf(menuItems[0]) + widthOf(menuItems[0]) + 20, yOf(menuItems[0]) + heightOf(itemsTexts[0]) + 10);
            setXY(menuItems[5], WIDTH / 2 + 20, yOf(menuItems[0]));
            setXY(itemsTexts[5], xOf(menuItems[5]) + widthOf(menuItems[5]) + 20, yOf(menuItems[5]) + heightOf(itemsTexts[5]) + 10);

            for (int i = 1; i < 5; i++) {
                setXY(menuItems[i], xOf(menuItems[i - 1]), yOf(menuItems[i - 1]) + heightOf(menuItems[i - 1]) + 10);
                setXY(itemsTexts[i], xOf(menuItems[i]) + widthOf(menuItems[i]) + 20, yOf(menuItems[i]) + heightOf(itemsTexts[i]) + 10);
            }
            for (int i = 6; i < 10; i++) {
                setXY(menuItems[i], WIDTH / 2 + 20, yOf(menuItems[i - 5]));
                setXY(itemsTexts[i], xOf(menuItems[i]) + widthOf(menuItems[i]) + 20, yOf(menuItems[i]) + heightOf(itemsTexts[i]) + 10);
            }
            setXY(itemsBackground, xOf(menuItems[0]) - 20, yOf(menuItems[0]) - 15);
        }

        void allocateLevelButtons() {
            setXY(classicLevelBtn, 190, 200);
            setXY(levelTexts[0], xOf(classicLevelBtn) + halfWidthOf(classicLevelBtn) - halfWidthOf(levelTexts[0]), yOf(classicLevelBtn) + 50);
            ButtonHandler.initClassicLevel(levelTexts[0], stage, gameScene, gameTimeline, countdownTimeline);

            setXY(magneticLevelBtn, xOf(classicLevelBtn) + widthOf(classicLevelBtn) + 70, yOf(classicLevelBtn));
            setXY(levelTexts[1], xOf(magneticLevelBtn) + halfWidthOf(magneticLevelBtn) - halfWidthOf(levelTexts[1]), yOf(magneticLevelBtn) + 50);
            ButtonHandler.initMagneticLevel(levelTexts[1], stage, gameScene, gameTimeline, countdownTimeline);

            setXY(spaceLevelBtn, xOf(magneticLevelBtn) + widthOf(magneticLevelBtn) + 70, yOf(classicLevelBtn));
            setXY(levelTexts[2], xOf(spaceLevelBtn) + halfWidthOf(spaceLevelBtn) - halfWidthOf(levelTexts[2]), yOf(spaceLevelBtn) + 50);
            ButtonHandler.initSpaceLevel(levelTexts[2], stage, gameScene, gameTimeline, countdownTimeline);

            setXY(returnBtn, xOf(classicLevelBtn), HEIGHT - heightOf(returnBtn) - 100);
            setXY(levelTexts[3], xOf(returnBtn) + halfWidthOf(returnBtn) - halfWidthOf(levelTexts[3]), yOf(returnBtn) + 50);
            ButtonHandler.initReturnButton(levelTexts[3]);

            setXY(levelTexts[4], 190, screenCenter.getY());
            setXY(levelTexts[5], 250, screenCenter.getY() + 60);
            setXY(levelTexts[6], 160, screenCenter.getY() + 120);
        }
    }

    public static class ItemHandler {

        public static void swapBats() {
            AudioHandler.itemPicked();
            batsSwapped = !batsSwapped;
            double aiX = xOf(ai), playerX = xOf(player);
            setX(player, aiX / 2);
            setX(ai, playerX);
            setX(player, aiX);
            renewWinratePanel();
        }

        public static void batResize(boolean increase) {
            AudioHandler.itemPicked();
            Rectangle whoPicked = playersBallTurn ? player : ai;
            if (increase) {
                setY(whoPicked, (yOf(whoPicked) + heightOf(whoPicked) + 10 >= HEIGHT - 2) ? yOf(whoPicked) - 10 : yOf(whoPicked) - 5);
                whoPicked.setHeight(heightOf(whoPicked) + 10);
            } else {
                if (heightOf(whoPicked) - 10 <= 5) {
                    whoPicked.setHeight(5);
                    renewWinratePanel();
                    return;
                }
                whoPicked.setHeight(heightOf(whoPicked) - 10);
                setY(whoPicked, yOf(whoPicked) + 5);
            }
            if (whoPicked == player) {
                if (!itemsPicked.contains(ImageHandler.getItemImage(increase ? 0 : 1)))
                    itemsPicked.add(ImageHandler.getItemImage(increase ? 0 : 1));
            } else {
                if (!ai.getItemsPicked().contains(ImageHandler.getItemImage(increase ? 0 : 1)))
                    ai.getItemsPicked().add(ImageHandler.getItemImage(increase ? 0 : 1));
            }
            renewWinratePanel();
        }

        public static void setBallSpeedScale(double scale) {
            AudioHandler.itemPicked();
            ballSpeedRatio *= scale;
            ballDX *= scale;
        }

        public static void changeBatSpeed(boolean speedUp) {
            AudioHandler.itemPicked();
            if (speedUp) {
                if (playersBallTurn) {
                    playerSpeedScale *= 1.16;
                    if (!itemsPicked.contains(ImageHandler.getItemImage(5)))
                        itemsPicked.add(ImageHandler.getItemImage(5));
                } else {
                    aiSpeedScale *= 1.16;
                    if (!ai.getItemsPicked().contains(ImageHandler.getItemImage(5)))
                        ai.getItemsPicked().add(ImageHandler.getItemImage(5));
                }
            } else {
                if (playersBallTurn) {
                    playerSpeedScale *= 0.84;
                    if (!itemsPicked.contains(ImageHandler.getItemImage(6)))
                        itemsPicked.add(ImageHandler.getItemImage(6));
                } else {
                    aiSpeedScale *= 0.84;
                    if (!ai.getItemsPicked().contains(ImageHandler.getItemImage(6)))
                        ai.getItemsPicked().add(ImageHandler.getItemImage(6));
                }
            }
            renewWinratePanel();
        }

        public static void timeStop() {
            AudioHandler.itemPicked();
            if (playersBallTurn) {
                hasTimeStopper = true;
                if (!itemsPicked.contains(ImageHandler.getItemImage(2)))
                    itemsPicked.add(ImageHandler.getItemImage(2));
            } else {
                ai.setTimeStopper(true);
                if (!ai.getItemsPicked().contains(ImageHandler.getItemImage(2)))
                    ai.getItemsPicked().add(ImageHandler.getItemImage(2));
            }
            renewWinratePanel();
        }

        public static void plusScore() {
            AudioHandler.itemPicked();
            if (playersBallTurn) {
                if (missesLeft == 4)
                    return;
                ++missesLeft;
                score[missesLeft] = ImageHandler.getSwitchImage(true);
                renewWinratePanel();
            } else {
                if (ai.getMissesLeft() == 4)
                    return;
                ai.setMissesLeft(ai.getMissesLeft() + 1);
                ai.getScore()[ai.getMissesLeft()] = ImageHandler.getSwitchImage(true);
                renewWinratePanel();
            }
        }

        public static void addPistol() {
            AudioHandler.pistolPicked();
            if (playersBallTurn) {
                hasPistol = true;
                pistolsLeft = 3;
                if (!itemsPicked.contains(ImageHandler.getItemImage(9)))
                    itemsPicked.add(ImageHandler.getItemImage(9));
            } else {
                ai.setHasPistol(true);
                ai.setShotsLeft(3);
                if (!ai.getItemsPicked().contains(ImageHandler.getItemImage(9)))
                    ai.getItemsPicked().add(ImageHandler.getItemImage(9));
            }
            renewWinratePanel();
        }
    }
}
