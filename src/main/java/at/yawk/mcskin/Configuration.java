package at.yawk.mcskin;

import at.yawk.mcskin.format.BufferedImageFormat;
import at.yawk.mcskin.format.ImageFormat;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * @author yawkat
 */
@Value
@Setter(AccessLevel.NONE)
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Configuration<I> {
    public static Configuration<BufferedImage> create() {
        return new Configuration<>(BufferedImageFormat.getInstance(), Duration.of(2, ChronoUnit.HOURS));
    }

    ImageFormat<I> format;
    Duration cacheDuration;

    public <N> Configuration<N> format(ImageFormat<N> format) {
        return new Configuration<>(format, this.cacheDuration);
    }

    public Configuration<I> cacheDuration(Duration duration) {
        return new Configuration<>(this.format, duration);
    }

    public Configuration<I> cacheDuration(long amount, TimeUnit unit) {
        return cacheDuration(Duration.ofMillis(unit.toMillis(amount)));
    }
}
