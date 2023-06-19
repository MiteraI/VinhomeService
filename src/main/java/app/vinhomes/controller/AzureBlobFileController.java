package app.vinhomes.controller;

import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.service.AzureBlobAdapter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
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

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping
    public ResponseEntity<String> upload
            (@RequestParam MultipartFile file, Model model)
            throws IOException {
        String fileName = azureBlobAdapter.upload(file);
        return ResponseEntity.ok(fileName);
    }

    @PostMapping("/avatar")
    //post to images/accountId/avatar.png which get account from session//
    public ResponseEntity<String> uploadAvatar
            (@RequestParam MultipartFile file, HttpSession session)
            throws IOException {
        Account sessionAccount = (Account) session.getAttribute("loginedUser");
        String fileName = azureBlobAdapter.uploadAvatar(file, session);
        return ResponseEntity.ok(fileName);
    }


    //THIS TO ENCODE THE PASSWORD OF ACCOUNT//
    //ONLY USE WHEN PASS IS READABLE AND NEED TO ENCODE//
    //AFTER ENCODE, THE PASS WILL BE UNREADABLE//
    //MAKE SURE TO CHANGE ACCID BELOW//
    @GetMapping("/encode")
    public void encode() {
        Account acc = accountRepository.findById(3L).get();
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        acc.setPassword(encoder.encode(acc.getPassword()));
        accountRepository.save(acc);
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

    @GetMapping("/getAvatar/{fileName}")
    public ResponseEntity<String> getAvatar
            (@PathVariable String fileName,
             HttpSession session) {

        Account sessionAccount = (Account) session.getAttribute("loginedUser");
        String url = azureBlobAdapter.getAvatar(fileName, session);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/getURLFolder/{fileName}")
    public ResponseEntity<String> getURL
            (@PathVariable String fileName) {

        String url = azureBlobAdapter.getURLFolder(fileName);
        return ResponseEntity.ok(url);
    }

}
