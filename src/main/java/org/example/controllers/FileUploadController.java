package org.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
public class FileUploadController {

    /**
     * Handles image file uploads
     * Saves to target directory where Spring serves static files from
     * @param file the uploaded file
     * @return the URL path to access the uploaded file
     */
    public String saveUploadedFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        // Generate unique filename to avoid conflicts
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        String projectPath = System.getProperty("user.dir");

        // Save to target/classes/static/images/uploads/ (where Spring serves from)
        String targetUploadDir = projectPath + "/target/classes/static/images/uploads/";
        Path targetUploadPath = Paths.get(targetUploadDir);
        if (!Files.exists(targetUploadPath)) {
            Files.createDirectories(targetUploadPath);
        }
        Path targetFilePath = targetUploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);

        // Return the URL path
        return "/images/uploads/" + uniqueFilename;
    }
}