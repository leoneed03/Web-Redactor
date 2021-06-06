package com.example.webredactor.controllers;

import com.example.webredactor.data.Drawable;
import com.example.webredactor.data.ImageData;
import com.example.webredactor.repositories.ImageRepo;
import com.example.webredactor.requests.MessageResponse;
import com.example.webredactor.tokens.ResponseToken;
import org.apache.commons.io.FileUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
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
    private ImageRepo imageRepo;

    private final Object mutexFileCreation = new Object();
    private final Object mutexFileCreationGrayscale = new Object();

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

    public ResponseEntity<?> constructResponseWithFile(String tempFileName, Mat imgGrayscale, String fileInitialName) throws FileNotFoundException {

        Imgcodecs.imwrite(tempFileName, imgGrayscale);
        File file = new File(tempFileName);

        try {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileInitialName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE,
                            "application/octet-stream")
                    .body(FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            e.printStackTrace();

            throw new FileNotFoundException("file " + fileInitialName + " not found");
        } finally {
            boolean fileIsDeleted = file.delete();
            assert fileIsDeleted;
        }
    }

    @GetMapping("to-grayscale/{fileIdLeft}")
    public ResponseEntity<?> downloadFileGrayscale(@PathVariable Long fileIdLeft) throws FileNotFoundException {

        Drawable imageFoundInRepo = imageRepo.getFileById(fileIdLeft);

        Mat imageSource = Imgcodecs.imdecode(new MatOfByte(imageFoundInRepo.getInnerFile()), Imgcodecs.IMREAD_UNCHANGED);

        Mat imgGrayscale = new Mat(imageSource.rows(), imageSource.cols(), CvType.CV_16UC1);
        Imgproc.cvtColor(imageSource, imgGrayscale, Imgproc.COLOR_RGB2GRAY);

        String tempImageName = "to_grayscale_" + imageFoundInRepo.getInitialName();

        synchronized (mutexFileCreationGrayscale) {
            return constructResponseWithFile(tempImageName, imgGrayscale, imageFoundInRepo.getInitialName());
        }
    }

    @GetMapping("merge/{fileIdLeft}&{fileIdRight}")
    public ResponseEntity<?> downloadFileMergedUpDown(@PathVariable Long fileIdLeft,
                                                      @PathVariable Long fileIdRight) throws FileNotFoundException {

        Drawable imageFoundLeft = imageRepo.getFileById(fileIdLeft);
        Drawable imageFoundRight = imageRepo.getFileById(fileIdRight);

        Mat imageSourceLeft = Imgcodecs.imdecode(new MatOfByte(imageFoundLeft.getInnerFile()), Imgcodecs.IMREAD_UNCHANGED);
        Mat imageSourceRight = Imgcodecs.imdecode(new MatOfByte(imageFoundRight.getInnerFile()), Imgcodecs.IMREAD_UNCHANGED);

        int colsMerged = Math.max(imageSourceLeft.cols(), imageSourceRight.cols());
        int rowsMerged = imageSourceLeft.rows() + imageSourceRight.rows();

        Mat resultMergedImage = new Mat(rowsMerged, colsMerged, CvType.CV_8UC3);

        Rect destRectLeft = new Rect(0, 0, imageSourceLeft.cols(), imageSourceLeft.rows());
        Rect destRectRight = new Rect(0, imageSourceLeft.rows(), imageSourceRight.cols(), imageSourceRight.rows());

        Mat destMatLeft = resultMergedImage.submat(destRectLeft);
        Mat destMatRight = resultMergedImage.submat(destRectRight);

        imageSourceLeft.copyTo(destMatLeft);
        imageSourceRight.copyTo(destMatRight);

        String tempImageName = "merged_" + imageFoundLeft.getInitialName();

        synchronized (mutexFileCreation) {
            return constructResponseWithFile(tempImageName, resultMergedImage, imageFoundLeft.getInitialName());
        }
    }
}