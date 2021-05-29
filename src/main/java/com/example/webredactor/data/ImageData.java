package com.example.webredactor.data;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ImageData implements Drawable {
    String initialName;
    byte[] file;

    public ImageData(MultipartFile file, String name) {

        System.out.println("Construct image " + file.getSize());
        try {
            this.file = file.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("cannot get inner bytes of multipart");
        }
        System.out.println("afgain onstruct image " + this.file.length);
        this.initialName = name;
    }

    @Override
    public String getInitialName() {
        return initialName;
    }

    @Override
    public byte[] getInnerFile() {
        System.out.println("get inner multipart " + file.length);
        return file;
    }
}
