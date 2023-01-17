package io.github.cunnydevelopment.cunnyaddon.utility.modules.external.gelbooru;

import java.util.ArrayList;
import java.util.List;

public class Posts {
    public List<Post> post = new ArrayList<>();

    public static class Post {
        public int width, height, preview_height, preview_width;
        public String source;
        public String file_url;
        public String preview_url;
        public String tags;
        public String md5;
        public String rating;
        public String ext = ".png";
    }
}
