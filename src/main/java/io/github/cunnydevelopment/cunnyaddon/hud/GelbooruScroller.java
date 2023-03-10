package io.github.cunnydevelopment.cunnyaddon.hud;

import io.github.cunnydevelopment.cunnyaddon.Cunny;
import io.github.cunnydevelopment.cunnyaddon.utility.Categories;
import io.github.cunnydevelopment.cunnyaddon.utility.FileSystem;
import io.github.cunnydevelopment.cunnyaddon.utility.PacketUtils;
import io.github.cunnydevelopment.cunnyaddon.utility.StringUtils;
import io.github.cunnydevelopment.cunnyaddon.utility.rendering.VisualUtils;
import io.github.cunnydevelopment.cunnyaddon.utility.rendering.gelbooru.GelbooruScraper;
import io.github.cunnydevelopment.cunnyaddon.utility.rendering.gelbooru.GelbooruUtil;
import io.github.cunnydevelopment.cunnyaddon.utility.rendering.gelbooru.Posts;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.hud.XAnchor;
import meteordevelopment.meteorclient.systems.hud.YAnchor;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class GelbooruScroller extends CunnyHud {
    private final GelbooruScraper gelbooruScraper = new GelbooruScraper();
    private final GelbooruUtil gelbooruUtil = new GelbooruUtil();
    private final List<String> tagTexts = new ArrayList<>();
    private boolean pausedUntilChange = false;
    //General
    private final SettingGroup sgImages = settings.createGroup("Images");
    public final Setting<Integer> tickDelay = sgImages.add(new IntSetting.Builder()
        .name("tick-delay")
        .description("The ticks before going to the next image.")
        .defaultValue(20)
        .sliderRange(20, 360)
        .build());
    public final Setting<String> tags = sgImages.add(new StringSetting.Builder()
        .name("tags")
        .description("The tags to input, treat like a gelbooru site.")
        .defaultValue("cum")
        .onChanged(s -> ticks = 20)
        .build());
    public final Setting<String> excludeTags = sgImages.add(new StringSetting.Builder()
        .name("exclude-tags")
        .description("The tags to ban, treat like a gelbooru site.")
        .defaultValue("furry")
        .onChanged(s -> ticks = 20)
        .build());
    public final Setting<GelbooruScraper.Rating> rating = sgImages.add(new EnumSetting.Builder<GelbooruScraper.Rating>()
        .name("rating")
        .description("Sets the rating.")
        .defaultValue(GelbooruScraper.Rating.None)
        .onChanged(r -> {
            gelbooruScraper.setRating(r);
            gelbooruScraper.reset();
            gelbooruScraper.load();
            pausedUntilChange = false;
        })
        .build());
    public final Setting<GelbooruScraper.ImageType> imageType = sgImages.add(new EnumSetting.Builder<GelbooruScraper.ImageType>()
        .name("image-type")
        .description("Sets the image type.")
        .defaultValue(GelbooruScraper.ImageType.Preview)
        .onChanged(type -> {
            gelbooruScraper.setType(type);
            gelbooruUtil.setType(type);
            pausedUntilChange = false;
        })
        .build());
    public final Setting<Double> scale = sgImages.add(new DoubleSetting.Builder()
        .name("scale")
        .description("The scale of the image.")
        .sliderRange(0.35, 3)
        .decimalPlaces(1)
        .defaultValue(1.5)
        .build());
    public final Setting<Integer> maxSize = sgImages.add(new IntSetting.Builder()
        .name("max-size")
        .description("The maximum size of images X by X.")
        .range(128, 1024)
        .sliderRange(128, 1024)
        .defaultValue(512)
        .onChanged(gelbooruUtil::setMaxSize)
        .build());
    //Tags
    private final SettingGroup sgTags = settings.createGroup("Tags");
    public final Setting<Boolean> showTags = sgTags.add(new BoolSetting.Builder()
        .name("show-tags")
        .description("Show tags under the image.")
        .defaultValue(true)
        .build());
    public final Setting<Integer> tagWidth = sgTags.add(new IntSetting.Builder()
        .name("tags-width")
        .description("The maximum width of tags.")
        .defaultValue(128 * 3)
        .build());
    public final Setting<Double> tagScale = sgTags.add(new DoubleSetting.Builder()
        .name("scale")
        .description("The scale of the tags text.")
        .sliderRange(0.50, 1.50)
        .decimalPlaces(2)
        .defaultValue(0.95)
        .build());
    //Saving
    private final SettingGroup sgSave = settings.createGroup("Saving");
    public final Setting<Boolean> autoSave = sgSave.add(new BoolSetting.Builder()
        .name("auto-save")
        .description("Automatically saves images.")
        .defaultValue(false)
        .build());

    public final Setting<Boolean> autoSend = sgSave.add(new BoolSetting.Builder()
        .name("auto-send")
        .description("Automatically sends the link in chat.")
        .defaultValue(false)
        .build());
    private String tagsString = "";
    public static final HudElementInfo<GelbooruScroller> INFO = new HudElementInfo<>(Categories.HUD, "gelbooru-scroller", "Scrolls gelbooru.", GelbooruScroller::new);
    private boolean waiting = false;
    private AbstractTexture currentImage;
    private int ticks = 0;
    private String lastSaved = "";
    public final Setting<Keybind> saveImage = sgSave.add(new KeybindSetting.Builder()
        .name("save")
        .description("Saves the current image to a unique file along with info about it.")
        .defaultValue(Keybind.none())
        .action(() -> {
            if (mc.currentScreen == null) new Thread(this::saveImage).start();
        })
        .build());

    public GelbooruScroller() {
        super(INFO);
        this.autoAnchors = false;
        this.box.xAnchor = XAnchor.Left;
        this.box.yAnchor = YAnchor.Top;
        setSize(128, 128);
    }

    @Override
    public void tick(HudRenderer renderer) {
        if (gelbooruScraper.posts == null || !Utils.canUpdate() || waiting) {
            if (gelbooruScraper.posts == null) gelbooruScraper.load();
            return;
        }

        if (ticks > 0) {
            ticks--;
            return;
        }

        if (!tags.get().equals(gelbooruScraper.rawTags) || !excludeTags.get().equals(gelbooruScraper.rawExcludedTags)) {
            gelbooruScraper.setTags(tags.get(), excludeTags.get());
            gelbooruScraper.reset();
            pausedUntilChange = !gelbooruScraper.load();
        } else if (!pausedUntilChange) {
            gelbooruScraper.nextPost();
        }

        if (!pausedUntilChange) {
            gelbooruUtil.setPost(gelbooruScraper.getPost());

            new Thread(this::loadImage).start();

            ticks = tickDelay.get();
        }
    }

    private void loadImage() {
        waiting = true;

        if (autoSend.get()) {
            PacketUtils.chat(StringUtils.purifyText(gelbooruUtil.getUrl() + " : <rwg>"));
        }

        try {
            currentImage = new NativeImageBackedTexture(NativeImage.read(gelbooruUtil.getImage()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mc.getTextureManager().registerTexture(VisualUtils.GELBOORU, currentImage);

        if (autoSave.get()) {
            saveImage();
        }

        tagTexts.clear();
        List<String> strings = new ArrayList<>();
        for (String str : gelbooruScraper.getPost().tags.split(" ")) {
            if (HudRenderer.INSTANCE.textWidth(String.join(", ", strings) + ", " + str, true, tagScale.get()) > tagWidth.get()) {
                tagTexts.add(String.join(", ", strings));
                strings.clear();
            }
            strings.add(str);
        }

        if (!strings.isEmpty()) {
            tagTexts.add(String.join(", ", strings));
        }

        tagsString = String.join("\n", tagTexts);

        waiting = false;
    }

    private void saveImage() {
        Posts.Post post = gelbooruScraper.getPost();
        lastSaved = post.md5;
        FileSystem.write(FileSystem.GELBOORU_PATH + "images/" + post.md5 + "." + post.ext, FileSystem.read(FileSystem.GELBOORU_PATH + "image." + post.ext));
        FileSystem.write(FileSystem.GELBOORU_PATH + "images/" + post.md5 + ".json", gelbooruScraper.gson.toJson(post, Posts.Post.class));
    }

    @Override
    public void render(HudRenderer renderer) {
        if (gelbooruScraper.posts == null || !Utils.canUpdate() || gelbooruScraper.posts.post.isEmpty()) {
            Cunny.LOG.info("Gelbooru Scroller can't render.");
            return;
        }

        if (currentImage != null) {
            setSize(gelbooruUtil.getFixedWidth() * scale.get(), gelbooruUtil.getFixedHeight() * scale.get());
            GL.bindTexture(VisualUtils.GELBOORU);
            Renderer2D.TEXTURE.begin();
            Renderer2D.TEXTURE.texQuad(box.x, box.y, gelbooruUtil.getFixedWidth() * scale.get(), gelbooruUtil.getFixedHeight() * scale.get(), Color.WHITE);
            Renderer2D.TEXTURE.render(null);
            if (lastSaved.equals(gelbooruScraper.getPost().md5)) {
                renderer.text("Image saved.", box.x + 15, box.y + gelbooruUtil.getFixedHeight() + 20, Color.GREEN, true, 1.1);
            }

            var splitTags = tagsString.split("\n");
            var cur = 1;

            for (String s : splitTags) {
                renderer.text(s + (splitTags.length - 1 >= cur ? "," : ""), box.x + (gelbooruUtil.getFixedWidth() * scale.get()) + 15, box.y + (renderer.textHeight(true, tagScale.get()) * (cur - 1)), Color.WHITE, true, tagScale.get());
                cur++;
            }
        }
    }
}
