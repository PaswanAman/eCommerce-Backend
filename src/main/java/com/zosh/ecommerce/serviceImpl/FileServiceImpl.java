package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.service.FileService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploaddir() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        String uploadDir;

        if (os.contains("win")){
            uploadDir = "static/image/";
        } else {
            uploadDir = "/opt/images";
        }

        Resource resource = new FileSystemResource(uploadDir);
        String resourcePath = URLDecoder.decode(resource.getFile().getAbsolutePath());
        resourcePath = resourcePath.replace(File.separator,"/");
        File dir = new File(resourcePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        return resourcePath;
    }


    @Override
    public InputStream getResource(String path, String fileName) throws IOException {
        String fullPath = uploaddir() + File.separator + fileName;
        InputStream is = new FileInputStream(fullPath);
        return is;
    }


    @Override
    public String savePicture(MultipartFile pictureFile) throws IOException {
        String uploadDir = uploaddir();

        String pictureFileName = generateUniqueFileName(pictureFile.getOriginalFilename());
        Path filePath = Paths.get(uploadDir, pictureFileName);

        Files.copy(pictureFile.getInputStream(), filePath);

        return pictureFileName;
    }


    @Override
    public String uploadImage(MultipartFile image) throws IOException {
        String uploadDir = uploaddir();

        String imageFileName = generateUniqueFileName(image.getOriginalFilename());
        Path filePath = Paths.get(uploadDir, imageFileName);

        Files.copy(image.getInputStream(), filePath);

        return imageFileName;
    }


    private String generateUniqueFileName(String originalFilename) {
        String fileExtension = StringUtils.getFilenameExtension(originalFilename);
        String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;
        return uniqueFileName;
    }
}
