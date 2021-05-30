package com.example.webredactor.controllers;

import com.example.webredactor.data.Drawable;
import com.example.webredactor.data.ImageData;
import com.example.webredactor.repositories.ImageRepo;
import com.example.webredactor.repositories.ImageRepoHashtable;
import com.example.webredactor.requests.MessageResponse;
import com.example.webredactor.tokens.ResponseToken;
import org.apache.commons.io.FileUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class ImageController {
    @Autowired
    ImageRepo imageRepo;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {

            ResponseToken responseToken = imageRepo.addFile(new ImageData(file, file.getOriginalFilename()));

            return ResponseEntity.ok().body(new MessageResponse("Files uploaded "
                    + file.getOriginalFilename()
                    + " "
                    + responseToken.getUniqueId(), responseToken.getUniqueId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Failed to upload files", -1));
        }
    }


    @GetMapping("download/{fileIdLeft}")
    public ResponseEntity<?> downloadFile(@PathVariable Long fileIdLeft) throws FileNotFoundException {

        Drawable imageFoundLeft = imageRepo.getFileById(fileIdLeft);

        byte[] imageInnerFileLeft = imageFoundLeft.getInnerFile();


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + imageFoundLeft.getInitialName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE,
                        "application/octet-stream")
                .body(imageInnerFileLeft);

    }

    @GetMapping("merge/{fileIdLeft}&{fileIdRight}")
    public ResponseEntity<?> downloadFileMergedUpDown(@PathVariable Long fileIdLeft,
                                                      @PathVariable Long fileIdRight) throws FileNotFoundException {

        Drawable imageFoundLeft = imageRepo.getFileById(fileIdLeft);
        Drawable imageFoundRight = imageRepo.getFileById(fileIdRight);

        Mat img1 = Imgcodecs.imdecode(new MatOfByte(imageFoundLeft.getInnerFile()), Imgcodecs.IMREAD_UNCHANGED);
        Mat img2 = Imgcodecs.imdecode(new MatOfByte(imageFoundRight.getInnerFile()), Imgcodecs.IMREAD_UNCHANGED);

        int cols = Math.max(img1.cols(), img2.cols());
        int rows = img1.rows() + img2.rows();

        Mat res = new Mat(rows, cols, CvType.CV_8UC3);

        Rect rect1 = new Rect(0, 0, img1.cols(), img1.rows());
        Rect rect2 = new Rect(0, img1.rows(), img2.cols(), img2.rows());

        Mat dest1 = res.submat(rect1);
        Mat dest2 = res.submat(rect2);

        img1.copyTo(dest1);
        img2.copyTo(dest2);

        synchronized (imageRepo) {
            String tempImageName = "merged_" + imageFoundLeft.getInitialName();

            Imgcodecs.imwrite(tempImageName, res);

            File file = new File(tempImageName);

            try {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + imageFoundLeft.getInitialName() + "\"")
                        .header(HttpHeaders.CONTENT_TYPE,
                                "application/octet-stream")
                        .body(FileUtils.readFileToByteArray(file));
            } catch (IOException e) {
                e.printStackTrace();

                throw new FileNotFoundException("file " + imageFoundLeft.getInitialName() + " not found");
            } finally {
                boolean fileIsDeleted = file.delete();
                assert fileIsDeleted;
            }
        }
    }
}