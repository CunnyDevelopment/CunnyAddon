package io.github.cunnydevelopment.cunnyaddon.utility.modules.external.gelbooru;

import io.github.cunnydevelopment.cunnyaddon.utility.FileSystem;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

//CHAOS, MAY THE PEOPLE KNOW THE FEAR OF RESIZING IMAGES
public class GelbooruUtil {
    private Posts.Post post;
    private GelbooruScraper.ImageType imageType = GelbooruScraper.ImageType.Direct;
    private int maxSize = 256;

    public void setType(GelbooruScraper.ImageType type) {
        this.imageType = type;
    }

    public void setPost(Posts.Post post) {
        this.post = post;
    }

    public void setMaxSize(int i) {
        this.maxSize = i;
    }

    public String getUrl() {
        return imageType == GelbooruScraper.ImageType.Direct ? post.file_url : post.preview_url;
    }

    public InputStream getImage() {
        FileSystem.writeImage(getUrl(), FileSystem.GELBOORU_PATH + "image." + post.ext);
        return new ByteArrayInputStream(FileSystem.readRaw(FileSystem.GELBOORU_PATH + "image." + post.ext));
    }

    private int getHeight() {
        return imageType == GelbooruScraper.ImageType.Direct ? post.height : post.preview_height;
    }

    private int getWidth() {
        return imageType == GelbooruScraper.ImageType.Direct ? post.width : post.preview_width;
    }

    private double getWidthRatio() {
        return Math.floor(getWidth()) / Math.round(getHeight());
    }

    private double getHeightRatio() {
        return Math.floor(getHeight()) / Math.round(getWidth());
    }

    private double proxyHeightFix() {
        return getHeightRatio() * getFixedWidth();
    }

    public int getFixedHeight() {
        return Math.toIntExact(getWidth() > maxSize || getHeight() > maxSize ? (long) proxyHeightFix() : getHeight());
    }

    public int getFixedWidth() {
        return Math.toIntExact(getWidth() > maxSize || getHeight() > maxSize ? Math.round(getWidthRatio() * (256)) : getWidth());
    }
}
