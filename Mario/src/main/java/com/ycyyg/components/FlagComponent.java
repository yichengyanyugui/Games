package com.ycyyg.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.SensorCollisionHandler;
import com.ycyyg.data.FlagInfo;
import com.ycyyg.types.ObjectType;
import javafx.animation.Interpolator;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getAssetLoader;

public class FlagComponent extends Component {

    public static EventHandler<? super Event> eventHandler = Event::consume;
    public static EventType<KeyEvent> eventType = KeyEvent.ANY;
    private final int MAX_SCORE;
    private final int height;
    private final int width;
    private boolean onOnce = true;

    public FlagComponent() {
        FlagInfo flagInfo = getAssetLoader().loadJSON("data/flag.json", FlagInfo.class).get();
        height = flagInfo.height();
        width = flagInfo.width();
        MAX_SCORE = flagInfo.maxScore();
    }

    @Override
    public void onAdded() {
        initPhysics();
    }

    private void initPhysics() {
        // 碰撞体积
        BoundingShape shape = BoundingShape.box(width, height);
        HitBox hitBox = new HitBox(shape);
        entity.setType(ObjectType.FLAG);
        PhysicsComponent physics = new PhysicsComponent();
        physics.addSensor(hitBox, new SensorCollisionHandler() {

            private double viewHeight;

            @Override
            protected void onCollisionBegin(Entity other) {
                if (onOnce) {
                    addScore(other);
                    addScoreAnimation(other);
                    Entity flag = getGameWorld().getEntitiesByType(ObjectType.FLAG_FLAG).getFirst();
                    animationBuilder()
                            .interpolator(Interpolator.EASE_OUT)
                            .duration(Duration.seconds(1))
                            .translate(flag)
                            .alongPath(new Line(flag.getX(), flag.getY(), flag.getX(), viewHeight - flag.getHeight() * 2))
                            .buildAndPlay();
                }
            }

            private void addScore(Entity other) {
                int score;
                if (other.getY() <= 0) {
                    score = MAX_SCORE;
                } else {
                    viewHeight = height + entity.getY();
                    double percent = (FXGLMath.abs(other.getY() - viewHeight)) / viewHeight;
                    score = (int) (percent * MAX_SCORE);
                }
                getGameWorld().getProperties().increment("Score", score);
                onOnce = false;
            }

            private void addScoreAnimation(Entity other) {
                other.getComponent(PhysicsComponent.class).setVelocityX(0);
                getInput().mockKeyRelease(KeyCode.RIGHT);
                FXGL.getInput().addEventFilter(eventType, eventHandler);
                animationBuilder()
                        .interpolator(Interpolator.EASE_OUT)
                        .onFinished(() -> {
                            getInput().mockKeyPress(KeyCode.RIGHT);
                        })
                        .duration(Duration.seconds(1))
                        .translate(other)
                        .alongPath(new Line(other.getX(), other.getY(), other.getX(), viewHeight - other.getHeight()))
                        .buildAndPlay();
            }
        });
        entity.addComponent(physics);
    }


}
