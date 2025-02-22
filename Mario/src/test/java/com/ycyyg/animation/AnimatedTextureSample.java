package com.ycyyg.animation;

/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.animation.Interpolator;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AnimatedTextureSample extends GameApplication {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1480);
        settings.setHeight(1020);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        var interpolators = Interpolators.values();

        for (int i = 0; i < interpolators.length; i++) {
            var x = i % 7;
            var y = i / 7;

            spawnRobot(x * 150, y * 360, interpolators[i].EASE_OUT(), interpolators[i].toString());
        }

        spawnRobot(0, 360 * 2, false, false);
        spawnRobot(160, 360 * 2, true, false);
        spawnRobot(320, 360 * 2, false, true);
        spawnRobot(480, 360 * 2, true, true);
    }

    @Override
    protected void initUI() {
        var frameSpinner = new Spinner<Integer>(0, 23, 0);
        frameSpinner.setPrefWidth(100);

        var btn = new Button("Play from frame");
        btn.setOnAction(e -> {
            spawnRobotForFrame(900, 700, frameSpinner.getValue());
        });

        var vbox = new VBox(10, frameSpinner, btn);

        addUINode(vbox, 1230, 820);
    }

    private void spawnRobotForFrame(double x, double y, int startFrame) {
        var animChannel = new AnimationChannel(image("robot_roll.png"), 7, 275, 275, Duration.seconds(2.6), 0, 23);
        var animTexture = new AnimatedTexture(animChannel);
        animTexture.playFrom(startFrame);

        var e = entityBuilder()
                .at(x, y)
                .view(animTexture)
                .buildAndAttach();

        animTexture.setOnCycleFinished(() -> e.removeFromWorld());
    }

    private void spawnRobot(double x, double y, Interpolator interpolator, String name) {
        var text = addText(name, x + 120, y + 30);
        text.fontProperty().unbind();
        text.setFont(Font.font(18));

        var animChannel = new AnimationChannel(image("robot_roll.png"), 7, 275, 275, Duration.seconds(2.6), 0, 23);
        var animTexture = new AnimatedTexture(animChannel);
        animTexture.setInterpolator(interpolator);
        animTexture.loop();

        entityBuilder()
                .at(x, y)
                .view(animTexture)
                .buildAndAttach();
    }

    private void spawnRobot(double x, double y, boolean isReverse, boolean isLoop) {
        var text = addText(isReverse ? "Reverse" : "Play", x + 120, y + 30);
        text.fontProperty().unbind();
        text.setFont(Font.font(18));

        var animChannel = new AnimationChannel(image("robot_death.png"), 7, 275, 275, Duration.seconds(2.6), 0, 26);
        var animTexture = new AnimatedTexture(animChannel);

        if (isLoop) {
            if (isReverse) {
                animTexture.loopReverse();
            } else {
                animTexture.loop();
            }
        } else {
            if (isReverse) {
                animTexture.playReverse();
            } else {
                animTexture.play();
            }
        }

        entityBuilder()
                .at(x, y)
                .view(animTexture)
                .buildAndAttach();
    }
}
