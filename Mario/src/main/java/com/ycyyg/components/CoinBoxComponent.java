package com.ycyyg.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.SensorCollisionHandler;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.ycyyg.data.CoinBoxInfo;
import com.ycyyg.types.ObjectType;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;


public class CoinBoxComponent extends Component {

    private final ObjectType type;
    private final PhysicsComponent physics;
    private final int height;
    private final int width;
    private final Image image;
    private final int score;
    private final double duration;
    private boolean onOnce = true;

    public CoinBoxComponent() {
        CoinBoxInfo coinBoxInfo = FXGL.getAssetLoader().loadJSON("data/coin.json", CoinBoxInfo.class).get();
        physics = new PhysicsComponent();
        height = coinBoxInfo.height();
        width = coinBoxInfo.width();
        image = FXGL.image(coinBoxInfo.image());
        type = ObjectType.COIN;
        score = coinBoxInfo.score();
        duration = (double) coinBoxInfo.duration();
    }

    @Override
    public void onAdded() {
        initView();
        initPhysics();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        Texture onAddedTexture = new Texture(image);
        entity.getViewComponent().addChild(onAddedTexture);
    }

    /**
     * 初始化物理属性
     */
    private void initPhysics() {
        // 碰撞体积
        BoundingShape body = BoundingShape.box(width, height);
        HitBox bodyHitBox = new HitBox("body", body);
        addCoinBoxSensor();
        entity.setType(type);
        entity.getBoundingBoxComponent().addHitBox(bodyHitBox);
        entity.addComponent(new CollidableComponent(true));
        entity.addComponent(physics);
    }

    /**
     * 人物碰到金币传感器
     */
    private void addCoinBoxSensor() {
        BoundingShape bottom = BoundingShape.chain(new Point2D(5, height), new Point2D(width - 20, height));
        HitBox bottomHitBox = new HitBox("bottom", bottom);
        physics.addSensor(bottomHitBox, new SensorCollisionHandler() {
            @Override
            protected void onCollisionBegin(Entity other) {
                if (onOnce && other.getType() == ObjectType.PLAYER) {
                    FXGL.inc("Score", score); // 增加分数
                    onOnce = false; // 设置为已经碰撞过了
                    // 设置碰撞后的状态
                    Texture onRemovedTexture = new Texture(image);
                    Color color = Color.rgb(102, 102, 102);
                    entity.getViewComponent().clearChildren();
                    entity.getViewComponent().addChild(onRemovedTexture.multiplyColor(color));
                    addCoinAnimation();
                }
            }
        });
    }

    /**
     * 碰撞后金币动画
     */
    private void addCoinAnimation() {
        Texture texture = FXGL.texture("coin.png");
        AnimationChannel defaultChannel = new AnimationChannel(texture.getImage(), Duration.seconds(duration), 10);
        AnimatedTexture animatedTexture = new AnimatedTexture(defaultChannel);
        animatedTexture.loop();
        Point2D atPoint = new Point2D(entity.getX(), entity.getY() - entity.getHeight());
        Entity onEntity = entityBuilder()
                .at(atPoint)
                .view(animatedTexture)
                .buildAndAttach();
        FXGL.getEngineTimer().runOnceAfter(onEntity::removeFromWorld, Duration.seconds(duration)); // 设置金币存在时间
    }
}

