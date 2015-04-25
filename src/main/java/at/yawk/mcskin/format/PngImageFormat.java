package at.yawk.mcskin.format;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.imageio.ImageIO;
import lombok.Getter;

/**
 * @author yawkat
 */
public class PngImageFormat implements ImageFormat<byte[]> {
    @Getter private static PngImageFormat instance = new PngImageFormat();

    private PngImageFormat() {}

    @Override
    public byte[] save(BufferedImage image) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "PNG", buf);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return buf.toByteArray();
    }
}
