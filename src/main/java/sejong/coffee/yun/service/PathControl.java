package sejong.coffee.yun.service;

import lombok.Getter;

import java.io.File;
import java.nio.file.Paths;

@Getter
public enum PathControl {
    PATH(setOSEnvironmentHomePath()),
    OCR_IMAGE_PATH(setOcrImagePath("img_1.png"));

    final String path;

    PathControl(String path) {
        this.path = path;
    }

    private static String setOSEnvironmentHomePath() {
        String uploadPath = Paths.get(System.getProperty("user.home"), "thumbnail") + "/";

        return uploadPath.replaceAll("/", File.separator);
    }

    private static String setOcrImagePath(String photoName) {
        String path = String.valueOf(Paths.get(System.getProperty("user.home"), photoName));
        return path.replaceAll("/", File.separator);
    }
}
