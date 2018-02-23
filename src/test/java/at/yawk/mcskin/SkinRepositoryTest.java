package at.yawk.mcskin;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author yawkat
 */
public class SkinRepositoryTest {
    @Test
    public void testNormal() throws IOException {
        SkinRepository<BufferedImage> skinRepository = new SkinRepository<>(Configuration.create());
        BufferedImage skin = skinRepository.getSkin(
                UUID.fromString("c72e6dc5-3860-4f8e-97d2-a60a2de7a4cd"), "yawkat", i -> i);
        BufferedImage expected = ImageIO.read(SkinRepositoryTest.class.getResource("yawkat.png"));
        Assert.assertEquals(imageToBytes(skin), imageToBytes(expected));
    }

    @Test
    public void testFallback() throws IOException {
        SkinRepository<BufferedImage> skinRepository = new SkinRepository<>(Configuration.create());
        Assert.assertEquals(
                imageToBytes(skinRepository.getSkin(UUID.fromString("10000000-0000-0000-0000-000000000000"),
                                                    "test", i -> i)),
                imageToBytes(skinRepository.getSkin(UUID.fromString("20000000-0000-0000-0000-000000000000"),
                                                    "test", i -> i))
        );
    }

    private static byte[] imageToBytes(BufferedImage img) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", out);
        return out.toByteArray();
    }
}