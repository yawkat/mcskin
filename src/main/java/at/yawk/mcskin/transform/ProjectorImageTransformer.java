package at.yawk.mcskin.transform;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;

/**
 * @author yawkat
 */
class ProjectorImageTransformer {
    private final BufferedImage source;
    private final BufferedImage target;
    private final Graphics2D gfx;
    private final float widthScale;
    private final float heightScale;

    public ProjectorImageTransformer(BufferedImage source,
                                     int canvasWidth, int canvasHeight,
                                     int targetWidth, int targetHeight,
                                     boolean alpha) {
        this.source = source;
        this.target = new BufferedImage(
                targetWidth, targetHeight,
                alpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR
        );
        this.gfx = this.target.createGraphics();
        this.widthScale = (float) targetWidth / canvasWidth;
        this.heightScale = (float) targetHeight / canvasHeight;
    }

    public ProjectorImageTransformer project(int fx, int fy, int w, int h, int tx, int ty) {
        return project(fx, fy, w, h, tx, ty, w, h);
    }

    public ProjectorImageTransformer project(int fx, int fy, int fw, int fh, int tx, int ty, int tw, int th) {
        Image section;
        try {
            section = source.getSubimage(fx, fy, fw, fh);
        } catch (RasterFormatException e) {
            // not supported in image
            return this;
        }
        Image scaled = section.getScaledInstance(
                (int) (tw * widthScale), (int) (th * heightScale),
                Image.SCALE_AREA_AVERAGING
        );
        gfx.drawImage(
                scaled,
                (int) (tx * widthScale), (int) (ty * heightScale),
                (int) (tw * widthScale), (int) (th * heightScale),
                null
        );
        return this;
    }

    public BufferedImage build() {
        this.gfx.dispose();
        return target;
    }
}
