package com.suktha.controllers.file;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "*")
public class FileController {

    private static final String IMAGE_DIRECTORY = "C:/uploaded_images/";
    private static final String VOICE_DIRECTORY = "C:/uploaded_voices/";

    @GetMapping("/images/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) throws IOException {
        return getFileResource(IMAGE_DIRECTORY, imageName, MediaType.IMAGE_JPEG);
    }
    @GetMapping("/voice/{voiceName}")
    public ResponseEntity<Resource> getVoice(@PathVariable String voiceName) throws IOException {
        return getFileResource(VOICE_DIRECTORY, voiceName, MediaType.APPLICATION_OCTET_STREAM);
    }
    @GetMapping("/comment/images/{imageName}")
    public ResponseEntity<Resource> getCommentImage(@PathVariable String imageName) throws IOException {
        return getFileResource("C:/uploaded_images/employee/", imageName, MediaType.IMAGE_JPEG);
    }
    @GetMapping("/comment/voice/{voiceName}")
    public ResponseEntity<Resource> getCommentVoice(@PathVariable String voiceName) throws IOException {
        return getFileResource("C:/uploaded_voices/employee/", voiceName, MediaType.APPLICATION_OCTET_STREAM);
    }

    private ResponseEntity<Resource> getFileResource(String directory, String fileName, MediaType mediaType) throws IOException {
        Path filePath = Paths.get(directory).resolve(fileName);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
}
//    private static final String IMAGE_DIRECTORY = "C:/uploaded_images/";
//
//    @GetMapping("/{imageName}")
//    public ResponseEntity<Resource> getImage(@PathVariable String imageName) throws IOException {
//        Path imagePath = Paths.get(IMAGE_DIRECTORY).resolve(imageName);
//        if (!Files.exists(imagePath)) {
//            return ResponseEntity.notFound().build();
//        }
//
//        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(imagePath));
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG)
//                .body(resource);
//    }