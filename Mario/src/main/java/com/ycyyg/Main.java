package com.ycyyg;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.localization.Language;
import com.ycyyg.components.PlayerComponent;
import com.ycyyg.factories.ObjectFactory;
import com.ycyyg.factories.PlayerFactory;
import com.ycyyg.factories.SceneFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Main extends GameApplication {
    private Entity player;
    private Level currentLevel;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * 添加文字
     *
     * @param var      全局变量
     * @param toString 显示的文字
     * @param x        x坐标
     * @param y        y坐标
     */
    private static void addTextForUI(String var, String toString, int x, int y) {
        Text text = getUIFactoryService().newText(getip(var).asString(toString));
        text.setFill(Color.RED);
        text.setX(x);
        text.setY(y);
        addUINode(text);
    }

    /**
     * 初始化
     *
     * @param settings 设置权柄
     */
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Mario");// 设置标题
        settings.setHeight(720);// 设置高度
        settings.setWidth(1280);// 设置宽度
        settings.setAppIcon("application_ico.png");// 设置图标
        settings.setMainMenuEnabled(true);// 程序开始是否打开菜单
        settings.getSupportedLanguages().clear();// 清空语言列表
        settings.getSupportedLanguages().add(Language.CHINESE);// 添加中文
        settings.getSupportedLanguages().add(Language.ENGLISH);// 添加英文
        settings.setDefaultLanguage(Language.CHINESE);// 设置默认语言
    }

    @Override
    protected void onPreInit() {
        getSettings().setGlobalMusicVolume(0.25);
        loopBGM("BGM_dash_runner.wav");
    }

    /**
     * 初始化游戏对象
     */
    @Override
    protected void initGame() {
        // 初始化工厂
        getGameWorld().addEntityFactory(new SceneFactory());
        getGameWorld().addEntityFactory(new ObjectFactory());
        getGameWorld().addEntityFactory(new PlayerFactory());
        spawn("background");

        // 初始化关卡
        currentLevel = setLevelFromMap("tmx/level0.tmx");
        // 初始化实体
        player = spawn("player");
        // 显示实体碰撞体积
        // showBoundingBox(player);
        // 设置视口跟随
        setViewport(player);
    }

    @Override
    protected void initInput() {
        // 添加按键事件处理：左键和右键
        FXGL.getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).left();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.LEFT);

        FXGL.getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).right();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.RIGHT);

        // 添加跳跃控制
        FXGL.getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                player.getComponent(PlayerComponent.class).jump();
                play("jump.wav");
            }
        }, KeyCode.UP);
    }

    /**
     * 设置视口跟随
     *
     * @param entity 实体
     */
    private void setViewport(Entity entity) {
        // 获取游戏视口（Viewport）
        Viewport viewport = getGameScene().getViewport();
        // 设置视口的边界，防止摄像机超出地图范围
        viewport.setBounds(0, 0, currentLevel.getWidth(), getAppHeight());
        // 让摄像机居中跟随玩家
        viewport.bindToEntity(entity, getAppWidth() / 2.0, getAppHeight() / 2.0);
        // 设置“懒”模式，使摄像机平滑跟随玩家
        viewport.setLazy(false);
    }

    /**
     * 初始化碰撞处理程序、物理属性
     */
    @Override
    protected void initPhysics() {
    }

    /**
     * 在播放状态下调用每一帧
     *
     * @param tpf 每一帧的时间
     */
    @Override
    protected void onUpdate(double tpf) {
        set("position_x", (int) player.getX());
        set("position_y", (int) player.getY());
        set("FPS", (int) (1.0 / tpf));
    }

    /**
     * 可以重写以提供全局变量。
     *
     * @param vars 包含CVars（全局变量）的映射
     */
    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("position_x", 0);
        vars.put("position_y", 0);
        vars.put("FPS", 0);
        vars.put("Score", 0);
    }

    /**
     * 初始化UI对象
     */
    @Override
    protected void initUI() {
        addTextForUI("position_x", "玩家位置X: %d", 0, 20);
        addTextForUI("position_y", "玩家位置Y: %d", 0, 40);
        addTextForUI("FPS", "FPS: %d", 0, 60);
        addTextForUI("Score", "分数: %d", 0, 100);
    }
}
