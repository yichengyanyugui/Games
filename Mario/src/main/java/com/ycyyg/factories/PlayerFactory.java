package com.ycyyg.factories;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.ycyyg.components.PlayerComponent;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class PlayerFactory implements EntityFactory {
    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        return entityBuilder(data)
                .with(new PlayerComponent())
                .build();
    }
}
