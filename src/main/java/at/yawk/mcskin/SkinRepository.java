package at.yawk.mcskin;

import at.yawk.mcskin.transform.SkinTransformer;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;
import lombok.Value;

/**
 * @author yawkat
 */
public class SkinRepository<I> {
    private final Configuration<I> configuration;
    private final LoadingCache<Request, Optional<I>> cache;

    public SkinRepository(Configuration<I> configuration) {
        this.configuration = configuration;
        this.cache = CacheBuilder.newBuilder()
                .softValues()
                .expireAfterWrite(configuration.cacheDuration().getSeconds() * 1_000 +
                                  configuration.cacheDuration().getNano() / 1_000_000,
                                  TimeUnit.MILLISECONDS)
                .build(new CacheLoader<Request, Optional<I>>() {
                    @Override
                    public Optional<I> load(Request key) throws Exception {
                        return doLoad(key);
                    }
                });
    }

    public I getSkin(UUID uuid, String username, SkinTransformer transformer) {
        Request primaryRequest = new Request(username, transformer);
        Optional<I> primarySkin = cache.getUnchecked(primaryRequest);
        if (primarySkin.isPresent()) {
            return primarySkin.get();
        }

        String fallbackUsername = (uuid.getLeastSignificantBits() & 1) == 0 ? "steve" : "alex";
        return cache.getUnchecked(new Request(fallbackUsername, transformer)).get();
    }

    @SneakyThrows(MalformedURLException.class)
    private Optional<I> doLoad(Request key) {
        URL url;
        switch (key.username) {
        case "steve":
            url = new URL("https://minecraft.net/images/steve.png");
            break;
        case "alex":
            url = new URL("https://minecraft.net/images/alex.png");
            break;
        default:
            url = new URL("http://skins.minecraft.net/MinecraftSkins/" + key.username + ".png");
            break;
        }

        BufferedImage image;
        try (InputStream in = url.openStream()) {
            image = ImageIO.read(in);
        } catch (IOException e) {
            return Optional.empty();
        }
        image = key.transformer.transform(image);
        return Optional.of(configuration.format().save(image));
    }

    @Value
    private static class Request {
        private String username;
        private SkinTransformer transformer;
    }
}
