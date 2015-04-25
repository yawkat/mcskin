package at.yawk.mcskin.transform;

import java.awt.image.BufferedImage;
import lombok.Value;

/**
 * @author yawkat
 */
@Value
public final class AvatarSkinTransformer implements SkinTransformer {
    static final int HEAD_SIZE_MC = 8;

    private final int size;

    @Override
    public BufferedImage transform(BufferedImage fullSkin) {
        return new ProjectorImageTransformer(fullSkin, HEAD_SIZE_MC, HEAD_SIZE_MC, size, size, false)
                .project(8, 8, HEAD_SIZE_MC, HEAD_SIZE_MC,
                         0, 0, HEAD_SIZE_MC, HEAD_SIZE_MC)
                .build();
    }
}
