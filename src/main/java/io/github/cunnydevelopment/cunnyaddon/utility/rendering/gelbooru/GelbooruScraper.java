package io.github.cunnydevelopment.cunnyaddon.utility.rendering.gelbooru;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.cunnydevelopment.cunnyaddon.Cunny;
import io.github.cunnydevelopment.cunnyaddon.utility.FileSystem;

import java.util.ArrayList;

public class GelbooruScraper {
    public Gson gson;
    public Posts posts;
    public String rawTags = "";
    public String rawExcludedTags = "";
    private int page = 0;
    private int currentImage = 0;
    private String rating = "";
    private String tags = "";
    private ImageType imgType = ImageType.Direct;

    public GelbooruScraper() {
        this.gson = new GsonBuilder().create();
    }

    public boolean load() {
        FileSystem.writeUrl("https://gelbooru.com/index.php?page=dapi&s=post&q=index&json=1&tags="
                + tags
                + (rating.equals("") ? "" : "+rating%3a" + rating)
                + "&pid=" + page
                + "&limit=" + 100,
            FileSystem.GELBOORU_PATH + "posts.json");
        this.posts = gson.fromJson(FileSystem.read(FileSystem.GELBOORU_PATH + "posts.json"), Posts.class);
        currentImage = 0;
        if (this.posts == null) return false;
        if (FileSystem.read(FileSystem.GELBOORU_PATH + "posts.json").equals("Too deep! Pull it back some. Holy fuck.")
            || this.posts.post.isEmpty()) {
            if (page == 0) return false;
            page = 0;
            return load();
        }
        if (!getExt().equals("png") && !getExt().equals("jpg") && !getExt().equals("jpeg")) nextPost();
        page++;
        return true;
    }

    public String getExt() {
        posts.post.get(currentImage).ext = FileSystem.getExtension(imgType == ImageType.Direct ? getPost().file_url : getPost().preview_url);
        return FileSystem.getExtension(imgType == ImageType.Direct ? getPost().file_url : getPost().preview_url);
    }

    public void setTags(String str, String excl) {
        rawExcludedTags = excl;
        var newTags = new ArrayList<String>();
        for (String tag : excl.split(" ")) {
            newTags.add("-" + tag.toLowerCase());
        }

        rawTags = str;
        tags = String.join("+", str.split(" ")).toLowerCase();
        if (!newTags.isEmpty()) tags += (tags.isBlank() ? "" : "+") + String.join("+", newTags);
        Cunny.LOG.info(tags);
    }

    public void nextPost() {
        currentImage++;
        if (currentImage >= get().post.size() - 1 || posts.post.isEmpty()) {
            load();
        } else {
            if (!getExt().equals("png") && !getExt().equals("jpg") && !getExt().equals("jpeg")) nextPost();
        }
    }

    public void reset() {
        this.page = 0;
        this.currentImage = 0;
    }

    public Posts.Post getPost() {
        return posts.post.get(currentImage);
    }

    public Posts get() {
        return posts;
    }

    public void setRating(Rating r) {
        switch (r) {
            case None -> rating = "";
            case General -> rating = "general";
            case Sensitive -> rating = "sensitive";
            case Questionable -> rating = "questionable";
            case Explicit -> rating = "explicit";
        }
    }

    public void setType(ImageType type) {
        this.imgType = type;
    }

    public enum Rating {
        Explicit,
        Questionable,
        Sensitive,
        General,
        None
    }

    public enum ImageType {
        Preview,
        Direct
    }
}
