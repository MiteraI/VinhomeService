package app.vinhomes.controller;


import app.vinhomes.service.AzureBlobAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;


@RestController
@RequestMapping("/images")
public class AzureBlobFileController {

    @Autowired
    private AzureBlobAdapter azureBlobAdapter;

    @PostMapping
    public ResponseEntity<String> upload
            (@RequestParam MultipartFile file)
            throws IOException {
        String fileName = azureBlobAdapter.upload(file);
        //check empty, null and duplicate//
        if (fileName == null || fileName.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty or null");
        } else if (fileName.equals(file.getOriginalFilename())) {
            return ResponseEntity.badRequest().body("File name is duplicate");
        } else {
            return ResponseEntity.ok(fileName);
        }
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllBlobs() {

        List<String> items = azureBlobAdapter.listBlobs();
        return ResponseEntity.ok(items);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> delete
            (@RequestParam String fileName) {

        azureBlobAdapter.deleteBlob(fileName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> getFile
            (@RequestParam String fileName)
            throws URISyntaxException {

        ByteArrayResource resource =
                new ByteArrayResource(azureBlobAdapter
                        .getFile(fileName));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\""
                        + fileName + "\"");

        return ResponseEntity.ok().contentType(MediaType
                        .APPLICATION_OCTET_STREAM)
                .headers(headers).body(resource);
    }

    @GetMapping("/getURL/{fileName}")
    public ResponseEntity<String> getUrl
            (@PathVariable String fileName) {

        String url = azureBlobAdapter.getFileURL(fileName);
        return ResponseEntity.ok(url);
    }
}
