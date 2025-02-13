package com.ycyyg.data;

public record PlayerInfo(
        int frameHeight,
        String image,
        int framesPerRow,
        int frameWidth,
        int maxVelocityY,
        int maxJumpTimes,
        AnimationChannelInfo animationChannelInfo,
        String name,
        int maxVelocityX
) {
}
