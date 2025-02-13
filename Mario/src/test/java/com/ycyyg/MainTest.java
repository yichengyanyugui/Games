package com.ycyyg;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;


public class MainTest extends GameApplication {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {

    }

    @Override
    protected void initGame() {
        Rectangle rectangle = new Rectangle(100, 50);
        rectangle.setFill(Color.RED);
        Entity e = entityBuilder()
                .view(rectangle)
                .at(100, 100)
                .buildAndAttach();
        FXGL.animationBuilder()
                // interpolator drives the animation rate
                .interpolator(Interpolators.ELASTIC.EASE_OUT())// 插值器
                // common configurations
                .onCycleFinished(() -> System.out.println("Cycle finished"))
                .onFinished(() -> System.out.println("Anim finished"))
                .duration(Duration.seconds(1))
                .repeat(5)
                .scale(e)
                .origin(new Point2D(40, 40))
                .from(new Point2D(1, 1))
                .to(new Point2D(2, 2))
                .buildAndPlay();
    }
}
