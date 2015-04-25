package at.yawk.mcskin.format;

import java.awt.image.BufferedImage;

/**
 * @author yawkat
 */
public interface ImageFormat<I> {
    I save(BufferedImage image);
}
