package com.ycyyg.factories;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.ycyyg.components.CoinBoxComponent;
import com.ycyyg.components.DoorComponent;
import com.ycyyg.components.FlagComponent;
import com.ycyyg.types.ObjectType;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;


public class ObjectFactory implements EntityFactory {
    @Spawns("coin")
    public Entity newCoin(SpawnData data) {
        return entityBuilder(data)
                .with(new CoinBoxComponent())
                .build();
    }

    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        // 碰撞体积
        BoundingShape shape = BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"));
        HitBox hitBox = new HitBox(shape);

        return entityBuilder(data)
                .type(ObjectType.PLATFORM)
                .bbox(hitBox)
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("door")
    public Entity newDoor(SpawnData data) {
        // 碰撞体积
        return entityBuilder(data)
                .with(new DoorComponent())
                .build();
    }

    @Spawns("ground")
    public Entity newGround(SpawnData data) {
        // 碰撞体积
        BoundingShape shape = BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"));
        HitBox hitBox = new HitBox(shape);

        return entityBuilder(data)
                .type(ObjectType.GROUND)
                .bbox(hitBox)
                .with(new PhysicsComponent())
                // .view(new Rectangle(hitBox.getWidth(), hitBox.getHeight()))
                .build();
    }

    @Spawns("obstacle")
    public Entity newObstacle(SpawnData data) {
        // 碰撞体积
        BoundingShape shape = BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"));
        HitBox hitBox = new HitBox(shape);

        return entityBuilder(data)
                .type(ObjectType.OBSTACLE)
                .bbox(hitBox)
                .with(new PhysicsComponent())
                // .view(new Rectangle(hitBox.getWidth(), hitBox.getHeight()))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("flag")
    public Entity newFlag(SpawnData data) {
        return entityBuilder(data)
                .with(new FlagComponent())
                .build();
    }

    @Spawns("flag_flag")
    public Entity newFlagFlag(SpawnData data) {
        return entityBuilder(data)
                .type(ObjectType.FLAG_FLAG)
                .view("flag.png")
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .build();
    }
}