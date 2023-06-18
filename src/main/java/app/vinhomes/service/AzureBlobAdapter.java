package app.vinhomes.service;

import app.vinhomes.entity.Account;
import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AzureBlobAdapter {

    @Autowired
    BlobServiceClient blobServiceClient;

    @Autowired
    BlobContainerClient blobContainerClient;

    public String upload(MultipartFile multipartFile)
            throws IOException {

        if (multipartFile == null || multipartFile.isEmpty()) {
            System.out.println("File is empty");
            return null;
        }
        if (listBlobs().contains(multipartFile.getOriginalFilename())) {
            System.out.println("File name is duplicate");
            return null;
        }
        // Todo UUID
        BlobClient blob = blobContainerClient
                .getBlobClient(multipartFile.getOriginalFilename());
        //get file from images folder then upload to container images//
        blob.upload(multipartFile.getInputStream(),
                multipartFile.getSize());
        return multipartFile.getOriginalFilename();
    }
    public boolean uploadFileLeave(MultipartFile multipartFile)
            throws IOException {

        if (multipartFile == null || multipartFile.isEmpty()) {
            System.out.println("File is empty");
            return false;
        }
        if (listBlobs().contains(multipartFile.getOriginalFilename())) {
            System.out.println("File name is duplicate");
            return false;
        }
        // Todo UUID
        BlobClient blob = blobContainerClient
                .getBlobClient(multipartFile.getOriginalFilename());
        //get file from images folder then upload to container images//
        blob.upload(multipartFile.getInputStream(),
                multipartFile.getSize());
        return true;
    }

    //UPLOAD AVATAR TO CONTAINER images BUT ALWAYS NAMED avatar.png based on images/accountId/avatar.png//
    public String uploadAvatar(MultipartFile multipartFile, HttpSession session)
            throws IOException {
        //get session//

        Account sessionAccount = (Account) session.getAttribute("loginedUser");

        //create blob if not exist//
        blobContainerClient = blobServiceClient.getBlobContainerClient("images/" + sessionAccount.getAccountId());

        //check null and empty//
        if (multipartFile == null || multipartFile.isEmpty()) {
            System.out.println("File is empty");
            return null;
        }

        BlobClient blob = blobContainerClient
                .getBlobClient("avatar.png");
        //get file from images folder then upload to container images//
        blob.deleteIfExists();
        blob.upload(multipartFile.getInputStream(),
                multipartFile.getSize());
        return multipartFile.getOriginalFilename();
    }

    public byte[] getFile(String fileName)
            throws URISyntaxException {

        BlobClient blob = blobContainerClient.getBlobClient(fileName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blob.download(outputStream);
        final byte[] bytes = outputStream.toByteArray();
        return bytes;

    }

    public List<String> listBlobs() {

        PagedIterable<BlobItem> items = blobContainerClient.listBlobs();
        List<String> names = new ArrayList<String>();
        for (BlobItem item : items) {
            names.add(item.getName());
        }
        return names;

    }

    public Boolean deleteBlob(String blobName) {

        BlobClient blob = blobContainerClient.getBlobClient(blobName);
        blob.delete();
        return true;
    }

    public String getFileURL(String filename) {
        return blobContainerClient.getBlobClient(filename).getBlobUrl();
    }

    public String getAvatar(String filename, HttpSession session) {
        //move container to subfolder images/accountId//
        Account sessionAccount = (Account) session.getAttribute("loginedUser");
        blobContainerClient = blobServiceClient.getBlobContainerClient("images/" + sessionAccount.getAccountId());
        return blobContainerClient.getBlobClient(filename).getBlobUrl();
    }

    public String getURLFolder(String filename) {
        blobContainerClient = blobServiceClient.getBlobContainerClient("images/leave/");
        return blobContainerClient.getBlobClient(filename).getBlobUrl();
    }


}