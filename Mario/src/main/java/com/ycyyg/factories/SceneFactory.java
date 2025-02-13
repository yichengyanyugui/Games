package com.ycyyg.factories;

import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Orientation;
import javafx.scene.image.Image;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class SceneFactory implements EntityFactory {
    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        double viewWidth = getAppWidth();
        double viewHeight = getAppHeight();
        Texture texture = texture("forest_background.png");
        texture.resize(viewHeight, viewWidth);
        Image image = texture.getImage();
        Orientation orientation = Orientation.HORIZONTAL;// 水平移动
        double speed = 1.0;// 滚动速度
        ScrollingBackgroundView backgroundView = new ScrollingBackgroundView(image, viewWidth, viewHeight, orientation, speed);
        return entityBuilder()
                .view(backgroundView)
                .zIndex(-10)
                .with(new IrremovableComponent())
                .build();
    }
}
