package com.TaskManager.controllers;

import com.TaskManager.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app")
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    public String upload(@RequestParam("file") MultipartFile multipartFile) {
        return imageService.upload(multipartFile);
    }

    @DeleteMapping
    public boolean delete(@RequestBody String url) throws IOException {
        return imageService.deleteImage(url);
    }
}
