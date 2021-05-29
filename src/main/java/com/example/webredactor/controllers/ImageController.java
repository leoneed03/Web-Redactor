package com.example.webredactor.controllers;

import com.example.webredactor.data.Drawable;
import com.example.webredactor.data.ImageData;
import com.example.webredactor.repositories.ImageRepo;
import com.example.webredactor.repositories.ImageRepoHashtable;
import com.example.webredactor.requests.MessageResponse;
import com.example.webredactor.tokens.ResponseToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Hashtable;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class ImageController {

    ImageRepoHashtable imageRepo = new ImageRepoHashtable();

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("uploaded file");
            System.out.println(file.getOriginalFilename());

            ResponseToken responseToken = imageRepo.addFile(new ImageData(file, file.getOriginalFilename()));

            return ResponseEntity.ok().body(new MessageResponse("Успешно загружены файлы: "
                    + file.getOriginalFilename()
                    + " "
                    + responseToken.getUniqueId(), responseToken.getUniqueId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Ошибка при загрузке файлов", -1));
        }
    }


    @GetMapping("download/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable Long fileId) {
        {
            System.out.println("for every in: " + imageRepo.imageByToken.size());
            (imageRepo.imageByToken).forEach((k, v) -> {
                System.out.println(k + " " + v.getInnerFile().length);
            });
            System.out.println("finished");
            Drawable imageFound = imageRepo.getFileById(fileId);
            System.out.println("try read");
            System.out.println("found image: " + imageFound.getInitialName());
            assert imageFound != null;
            byte[] imageInnerFile = imageFound.getInnerFile();
            assert imageInnerFile != null;

            System.out.println("try read binary: " + imageInnerFile.length);

            System.out.println("read binary");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageFound.getInitialName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .body(imageInnerFile);
        }
    }

    @GetMapping("/upload-form")
    public String upload(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        System.out.println("trying to upload-form");
        model.addAttribute("name", name);
//        return "upload-form";

        return "markdown";
    }

}