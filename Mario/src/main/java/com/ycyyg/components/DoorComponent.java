package com.ycyyg.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.SensorCollisionHandler;
import com.ycyyg.data.DoorInfo;
import com.ycyyg.types.ObjectType;
import javafx.scene.input.KeyCode;

public class DoorComponent extends Component {
    private final int bHeight;
    private final int bWidth;

    public DoorComponent() {
        DoorInfo doorInfo = FXGL.getAssetLoader().loadJSON("data/door.json", DoorInfo.class).get();
        bHeight = doorInfo.bHeight();
        bWidth = doorInfo.bWidth();
    }

    /**
     *
     */
    @Override
    public void onAdded() {
        entity.setType(ObjectType.DOOR);
        PhysicsComponent physics = new PhysicsComponent();
        HitBox hitBox = new HitBox(BoundingShape.box(bWidth, bHeight));
        physics.addSensor(hitBox, new SensorCollisionHandler() {
            @Override
            protected void onCollisionBegin(Entity other) {
                FXGL.getInput().mockKeyRelease(KeyCode.RIGHT);
                FXGL.getInput().removeEventFilter(FlagComponent.eventType, FlagComponent.eventHandler);
                FXGL.showMessage("你赢啦!", () -> {
                    FXGL.getGameController().gotoMainMenu();
                });
            }
        });
        entity.addComponent(physics);
    }
}
