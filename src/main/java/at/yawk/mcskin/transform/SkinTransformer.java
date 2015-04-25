package at.yawk.mcskin.transform;

import java.awt.image.BufferedImage;

/**
 * @author yawkat
 */
public interface SkinTransformer {
    BufferedImage transform(BufferedImage fullSkin);
}
