package com.example.webredactor.controllers;

import com.example.webredactor.requests.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
public class ImageController {

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("uploaded file");
            System.out.println(file.getOriginalFilename());

            return ResponseEntity.ok().body(new MessageResponse("Успешно загружены файлы: " + file.getOriginalFilename()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Ошибка при загрузке файлов"));
        }
    }
    @GetMapping("/upload-form")
    public String upload(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        System.out.println("trying to upload-form");
        model.addAttribute("name", name);
//        return "upload-form";

        return "markdown";
    }

}