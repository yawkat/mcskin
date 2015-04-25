package at.yawk.mcskin.transform;

import java.awt.image.BufferedImage;
import lombok.Value;

/**
 * @author yawkat
 */
@Value
public final class FrontViewSkinTransformer implements SkinTransformer {
    static final int HEAD_SIZE_MC = 8;

    @Override
    public BufferedImage transform(BufferedImage fullSkin) {
        return new ProjectorImageTransformer(fullSkin, 16, 32, 64, 128, true)
                // base
                .project(8, 8, 8, 8, // head
                         4, 0)
                .project(20, 20, 8, 12, // torso
                         4, 8)
                .project(4, 20, 4, 12, // left arm legacy
                         0, 8)
                .project(44, 20, 4, 12, // right arm
                         12, 8)
                .project(4, 20, 4, 12, // left leg legacy
                         4, 20)
                .project(4, 20, 4, 12, // right leg
                         8, 20)
                .project(36, 52, 4, 12, // left arm 1.8+
                         0, 8)
                .project(20, 52, 4, 12, // left leg 1.8+
                         8, 20)

                        // overlay
                .project(40, 8, 8, 8, // head
                         4, 0)
                .project(20, 36, 8, 12, // torso
                         4, 8)
                .project(44, 36, 4, 12, // right arm
                         12, 8)
                .project(4, 36, 4, 12, // right leg
                         8, 20)
                .project(52, 52, 4, 12, // left arm
                         0, 8)
                .project(4, 52, 4, 12, // left leg
                         8, 20)
                .build();
    }
}
