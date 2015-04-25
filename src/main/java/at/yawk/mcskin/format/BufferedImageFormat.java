package at.yawk.mcskin.format;

import java.awt.image.BufferedImage;
import lombok.Getter;

/**
 * @author yawkat
 */
public final class BufferedImageFormat implements ImageFormat<BufferedImage> {
    @Getter private static BufferedImageFormat instance = new BufferedImageFormat();

    private BufferedImageFormat() {}

    @Override
    public BufferedImage save(BufferedImage image) {
        return image;
    }
}
