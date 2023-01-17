package io.github.cunnydevelopment.cunnyaddon.utility;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;

public class FileSystem {
    public static final String CUNNY_PATH = System.getProperty("user.dir") + "/cunny/";
    public static final String COMPATIBILITY_PATH = System.getProperty("user.dir") + "/cunny/compatibility/";
    public static final String GELBOORU_PATH = System.getProperty("user.dir") + "/cunny/gelbooru/";

    public static final String SEPARATOR = System.getProperty("path.separator");

    public static void writeImage(String url, String path) {
        FileSystem.mkdir(path);
        try {
            InputStream inputStream = new URL(url).openStream();
            OutputStream outputStream = new FileOutputStream(path);

            byte[] b = new byte[2048];
            int length;

            while ((length = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, length);
            }

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getExtension(String path) {
        if (new File(path).isDirectory()) return "FOLDER";
        else if (new File(path).isFile()) {
            var pathParts = new File(path).getPath().split("/");
            var newPath = pathParts[pathParts.length - 1];
            var splitPath = newPath.split("\\.");
            return splitPath.length == 0 ? "txt" : splitPath[splitPath.length - 1];
        } else {
            var splitPath = path.split("\\.");
            return splitPath.length == 0 ? "txt" : splitPath[splitPath.length - 1];
        }
    }

    public static void writeUrl(String url, String path) {
        try {
            InputStream inputStream = new URL(url).openStream();
            write(path, new String(inputStream.readAllBytes()), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(String path, String value) {
        write(path, value, "UTF-8");
    }

    public static void write(String path, String value, String charset) {
        File file = new File(path);
        mkdir(file.getAbsolutePath());
        try {
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));
            outStream.write(value.getBytes(charset));
            outStream.close();

            FileInputStream fis = new FileInputStream(file);
            DataInputStream reader = new DataInputStream(fis);
            if (!Arrays.equals(reader.readAllBytes(), value.getBytes(charset))) {
                System.out.println(
                    "The content written to: " + file.getAbsolutePath() + " was invalid, please check it.");
            }
            reader.close();
        } catch (IOException e) {
            if (file.exists()) {
                file.deleteOnExit();
                System.out.println(
                    "Write attempt of: " + file.getAbsolutePath() + " was invalid, will be deleted.");
            } else {
                System.out.println(
                    "Write attempt of: " + file.getAbsolutePath() + " was invalid, wasn't written.");
            }
        }
    }

    public static String read(String path) {
        return read(new File(path));
    }

    public static String read(File file) {
        if (file.isDirectory()) {
            System.out.println(
                "The file at: "
                    + file.getAbsolutePath()
                    + " was attempted to be read and is a directory.");
            return "";
        }
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            return "";
        }
    }

    public static byte[] readRaw(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            System.out.println(
                "The file at: "
                    + file.getAbsolutePath()
                    + " was attempted to be read and is a directory.");
            return new byte[0];
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            return new byte[0];
        }
    }

    public static boolean exists(String path) {
        return new File(path).exists();
    }

    public static void mkdir(String path) {
        String newPath = path;
        String[] splitPath = newPath.split(SEPARATOR);
        if (splitPath[splitPath.length - 1].contains(".")) {
            newPath = new File(newPath).getParent();
        }
        new File(newPath).mkdirs();
    }
}
