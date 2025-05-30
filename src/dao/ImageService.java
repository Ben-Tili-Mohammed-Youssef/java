package dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class ImageService {
    private static final String IMAGE_DIR = "article_images/";

    static {
        // Créer le dossier s'il n'existe pas
        new File(IMAGE_DIR).mkdirs();
    }

    public static String saveImage(File sourceFile) throws IOException {
        // Générer un nom de fichier unique
        String uniqueName = "img_" + UUID.randomUUID() + getFileExtension(sourceFile.getName());
        Path targetPath = Paths.get(IMAGE_DIR, uniqueName);

        // Copier le fichier
        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueName; // Retourne juste le nom du fichier
    }

    public static String getImagePath(String imageName) {
        return IMAGE_DIR + imageName;
    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }
}