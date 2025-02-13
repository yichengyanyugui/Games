package com.ycyyg.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.ycyyg.constants.CONSTANTS;
import com.ycyyg.data.PlayerInfo;
import com.ycyyg.types.ObjectType;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

// 处理玩家输入和角色控制。
// 管理玩家状态（如生命值、得分等）。
// 与其他游戏组件（如物理引擎）交互。
// 控制动画和视觉效果。
// 触发事件（例如攻击、跳跃、死亡）。


public class PlayerComponent extends Component {
    private final PhysicsComponent physics;
    private final PlayerInfo playerInfo;
    private final ObjectType type;
    private final AnimationChannel idle;
    private final AnimationChannel walk;
    private final AnimatedTexture texture;
    private final int framesPerRow;
    private final int frameWidth;
    private final int frameHeight;
    private final Image image;
    private final int MAX_VELOCITY_Y;
    private final int MAX_VELOCITY_X;
    private final int MAX_JUMP_TIMES;
    private int jumpTimes;
    private int maxHeight;

    public PlayerComponent() {
        // 获取json数据
        playerInfo = getAssetLoader().loadJSON("data/player.json", PlayerInfo.class).get();
        physics = new PhysicsComponent(); // 初始化物理组件
        image = FXGL.image(playerInfo.image());
        framesPerRow = playerInfo.framesPerRow();
        frameWidth = playerInfo.frameWidth();
        frameHeight = playerInfo.frameHeight();
        jumpTimes = playerInfo.maxJumpTimes();
        MAX_VELOCITY_X = playerInfo.maxVelocityX();
        MAX_VELOCITY_Y = playerInfo.maxVelocityY();
        MAX_JUMP_TIMES = playerInfo.maxJumpTimes();
        type = ObjectType.PLAYER;
        idle = createAnimationChannel("idle");// 站立动画
        walk = createAnimationChannel("walk");// 行走动画
        texture = new AnimatedTexture(idle);// 初始状态
        texture.loop();// 播放动画
    }

    private static void resetPosition() {
        Entity entity = getGameWorld().getEntitiesByType(ObjectType.PLAYER).get(0);
        Point2D point2D = new Point2D(CONSTANTS.START_X, CONSTANTS.START_Y);
        getGameWorld().getProperties().setValue("Score", 0);
        entity.getComponent(PhysicsComponent.class).overwritePosition(point2D);
        entity.setScaleX(1);
    }

    /**
     * 当组件添加时
     */
    @Override
    public void onAdded() {
        initPhysics();// 初始化物理属性
        initViewComponent();// 初始化组件属性
    }

    /**
     * 创建精灵动画
     *
     * @param state 人物当前状态
     * @return 人物动画信息
     */
    private AnimationChannel createAnimationChannel(String state) {
        int startFrame;
        Duration channelDuration;
        int endFrame;
        if (state.equals("idle")) {
            channelDuration = Duration.seconds((double) playerInfo.animationChannelInfo().idle().duration());
            startFrame = playerInfo.animationChannelInfo().idle().startFrame();
            endFrame = playerInfo.animationChannelInfo().idle().endFrame();
        } else {
            channelDuration = Duration.seconds((double) playerInfo.animationChannelInfo().walk().duration());
            startFrame = playerInfo.animationChannelInfo().walk().startFrame();
            endFrame = playerInfo.animationChannelInfo().walk().endFrame();
        }
        return new AnimationChannel(image, framesPerRow, frameWidth, frameHeight, channelDuration, startFrame, endFrame);
    }

    @Override
    public void onUpdate(double tpf) {
        if (physics.isMovingX()) {
            if (texture.getAnimationChannel() != walk) {
                texture.loopAnimationChannel(walk);
            }
        } else {
            if (texture.getAnimationChannel() != idle) {
                texture.loopAnimationChannel(idle);
            }
        }
        if (entity.getY() > FXGL.getAppHeight()) {
            resetPosition();
        }
    }

    private void initViewComponent() {
        // 将 AnimatedTexture 添加为实体的视图组件的子元素。
        // 这意味着该实体的视觉效果将显示 texture（动画图像）
        entity.getViewComponent().addChild(texture);
    }

    private void initPhysics() {
        // 物体碰撞
        HitBox hitBox = new HitBox(BoundingShape.box(frameWidth, frameHeight));
        entity.getBoundingBoxComponent().addHitBox(hitBox);
        // 设置物理属性
        physics.setBodyType(BodyType.DYNAMIC);
        // 添加地面传感器
        HitBox groundSensor = new HitBox("GROUND_SENSOR", new Point2D(frameWidth / 2.0 - 7.5, frameHeight - 5), BoundingShape.box(15, 5));
        physics.addGroundSensor(groundSensor);
        // 监听实体的 onGround 属性，当实体站在地面上时，会重置跳跃最高距离
        physics.onGroundProperty().addListener((obs, old, isOnGround) -> {
            // 如果实体站在地面上，恢复最高距离
            if (isOnGround) {
                jumpTimes = MAX_JUMP_TIMES;
                play("land.wav");
            }
        });
        // 设置摩擦力
        physics.setFixtureDef(new FixtureDef().friction(0.0f));// 防止黏在墙上
        // 设置物体位置
        entity.setPosition(CONSTANTS.START_X, CONSTANTS.START_Y);
        // 添加物理组件
        entity.addComponent(physics);
        entity.addComponent(new CollidableComponent(true));
        entity.setType(type);
    }

    public void left() {
        entity.setScaleX(-1); // 设置比例: 镜像 1:1
        physics.setVelocityX(-MAX_VELOCITY_X); // 设置水平速度
    }

    public void right() {
        entity.setScaleX(1); // 设置比例: 镜像 1:1
        physics.setVelocityX(MAX_VELOCITY_X); // 设置水平速度

    }

    public void stop() {
        physics.setVelocityX(0);
    }

    public void jump() {
        if (jumpTimes == 0) {
            return;
        }
        physics.setVelocityY(MAX_VELOCITY_Y);
        jumpTimes--;
    }

}




