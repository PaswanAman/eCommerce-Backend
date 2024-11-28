package com.zosh.ecommerce.service;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Service
public interface FileService {
    String uploaddir() throws IOException;
    InputStream getResource(String path, String fileName) throws IOException, FileNotFoundException;
    String savePicture(MultipartFile pictureFile) throws IOException;

//    boolean isPictureFile();
    String uploadImage(MultipartFile image) throws IOException;
    public byte[] loadPicture(String picturePath) throws IOException;
}
