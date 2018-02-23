package at.yawk.mcskin;

import at.yawk.mcskin.transform.SkinTransformer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final UUID STEVE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID ALEX_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");

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
        Request primaryRequest = new Request(uuid, transformer);
        Optional<I> primarySkin = cache.getUnchecked(primaryRequest);
        if (primarySkin.isPresent()) {
            return primarySkin.get();
        }

        UUID fallbackUuid = (uuid.getLeastSignificantBits() & 1) == 0 ? STEVE_UUID : ALEX_UUID;
        return cache.getUnchecked(new Request(fallbackUuid, transformer)).get();
    }

    @SneakyThrows(MalformedURLException.class)
    private Optional<I> doLoad(Request key) {
        URL url;
        if (key.uuid.equals(STEVE_UUID) || key.uuid.equals(ALEX_UUID)) {
            url = key.uuid.equals(STEVE_UUID) ?
                    new URL("http://assets.mojang.com/SkinTemplates/steve.png") :
                    new URL("http://assets.mojang.com/SkinTemplates/alex.png");
        } else {
            try {
                JsonNode profile = OBJECT_MAPPER.readTree(new URL(
                        "https://sessionserver.mojang.com/session/minecraft/profile/" +
                        key.uuid.toString().replace("-", "")));
                if (profile == null) { return Optional.empty(); }
                JsonNode textures = null;
                for (JsonNode property : profile.path("properties")) {
                    if ("textures".equals(property.path("name").textValue())) {
                        byte[] binary = property.path("value").binaryValue();
                        if (binary != null) {
                            textures = OBJECT_MAPPER.readTree(binary);
                        }
                        break;
                    }
                }
                if (textures == null) { return Optional.empty(); }

                String s = textures.path("textures").path("SKIN").path("url").textValue();
                if (s == null) { return Optional.empty(); }
                url = new URL(s);
            } catch (IOException e) {
                return Optional.empty();
            }
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
        private UUID uuid;
        private SkinTransformer transformer;
    }
}
