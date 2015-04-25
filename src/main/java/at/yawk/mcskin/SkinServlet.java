package at.yawk.mcskin;

import at.yawk.mcskin.format.PngImageFormat;
import at.yawk.mcskin.transform.AvatarSkinTransformer;
import at.yawk.mcskin.transform.FrontViewSkinTransformer;
import at.yawk.mcskin.transform.SkinTransformer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yawkat
 */
public class SkinServlet extends HttpServlet {
    private static final Pattern PATH_URL_PATTERN = Pattern.compile("/([0-9a-f\\-]+)/(\\w+)(/(.+))?");

    private final Map<String, SkinTransformer> transformers = new HashMap<>();
    private final SkinRepository<byte[]> repository;

    public SkinServlet() {
        this(new SkinRepository<>(Configuration.create().format(PngImageFormat.getInstance())));
    }

    public SkinServlet(SkinRepository<byte[]> repository) {
        this.repository = repository;

        AvatarSkinTransformer avatarTransformer = new AvatarSkinTransformer(8);
        setDefaultTransformer(avatarTransformer);
        addTransformer("avatar", avatarTransformer);

        addTransformer("avatar/scaled", new AvatarSkinTransformer(20));
        addTransformer("front", new FrontViewSkinTransformer());
    }

    public void setDefaultTransformer(SkinTransformer transformer) {
        addTransformer("", transformer);
    }

    public void addTransformer(String name, SkinTransformer transformer) {
        transformers.put(name.toLowerCase(), transformer);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        Matcher matcher = PATH_URL_PATTERN.matcher(path);
        if (!matcher.matches()) {
            resp.setStatus(404);
            return;
        }

        UUID uuid = UUID.fromString(matcher.group(1));
        String username = matcher.group(2);
        String transformerName = matcher.group(4);
        SkinTransformer transformer = transformers.get(
                transformerName == null ? "" : transformerName.toLowerCase());

        if (transformer == null) {
            resp.setStatus(404);
            return;
        }

        serve(resp, uuid, username, transformer);
    }

    protected void serve(HttpServletResponse resp, UUID uuid, String username, SkinTransformer transformer)
            throws IOException {
        byte[] skin = repository.getSkin(uuid, username, transformer);
        resp.setStatus(200);
        resp.setContentLength(skin.length);
        resp.setContentType("image/png");

        resp.getOutputStream().write(skin);
        resp.getOutputStream().close();
    }
}
