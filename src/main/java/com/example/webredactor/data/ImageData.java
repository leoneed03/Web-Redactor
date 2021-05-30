package com.example.webredactor.data;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ImageData implements Drawable {
    String initialName;
    byte[] file;

    public ImageData(MultipartFile file, String name) {

        try {
            this.file = file.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.initialName = name;
    }

    @Override
    public String getInitialName() {
        return initialName;
    }

    @Override
    public byte[] getInnerFile() {
        return file;
    }
}
