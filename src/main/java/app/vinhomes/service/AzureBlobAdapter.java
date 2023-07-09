package app.vinhomes.service;

import app.vinhomes.entity.Account;
import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Component
public class AzureBlobAdapter {

    @Autowired
    BlobServiceClient blobServiceClient;

    @Autowired
    BlobContainerClient blobContainerClient;
//    THIS CLASS IS USED TO UPLOAD AND DELETE FILE TO CONTAINER images BY KEEP THE FILE NAME
//    1. CHECK IF FILE IS EMPTY OR DUPLICATE
//    2. UPLOAD FILE TO CONTAINER images AND KEEP THE FILE NAME USING MULTIPARTFILE

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

    //UPLOAD AVATAR TO CONTAINER images BUT ALWAYS NAMED avatar.png based on images/accountId/avatar.png//
    //1. GET ACCOUNT FROM SESSION AND CHECK FOR EXIST FOLDER MATCH WITH ACCOUNT ID
    //2. IF NOT EXIST CREATE NEW FOLDER USING ACCOUNT ID AS NAME
    //3. UPLOAD FILE TO CONTAINER images/accountId/avatar.png AND KEEP THE FILE NAME USING MULTIPARTFILE (ANY FILES UPLOAD TO THIS WILL CHANGE NAME TO avatar.png)
    public String uploadAvatar(MultipartFile multipartFile, HttpSession session)
            throws IOException {
        //get session//

        Account sessionAccount = (Account) session.getAttribute("loginedUser");

        //create blob if not exist//
        blobContainerClient = blobServiceClient.getBlobContainerClient("images/avatar/" + sessionAccount.getAccountId());

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

    //THIS TO DOWNLOAD FILE FROM CONTAINER images//
    //1. GET FILE FROM CONTAINER images//
    //2. DOWNLOAD FILE FROM CONTAINER images//
    //download?fileName={name goes here}//
    public byte[] getFile(String fileName)
            throws URISyntaxException {

        BlobClient blob = blobContainerClient.getBlobClient(fileName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blob.download(outputStream);
        final byte[] bytes = outputStream.toByteArray();
        return bytes;

    }


    //GET LIST OF ALL BLOBS IN CONTAINER images//
    //1. GET ALL BLOBS IN CONTAINER images//
    //2. ADD ALL BLOBS NAME TO LIST//

    public List<String> listBlobs() {

        PagedIterable<BlobItem> items = blobContainerClient.listBlobs();
        List<String> names = new ArrayList<String>();
        for (BlobItem item : items) {
            names.add(item.getName());
        }
        return names;

    }

    //DELETE BLOB FROM CONTAINER images//
    //1. GET BLOB FROM CONTAINER images//
    //2. DELETE BLOB FROM CONTAINER images//
    //delete?fileName={name goes here}//
    public Boolean deleteBlob(String blobName) {

        BlobClient blob = blobContainerClient.getBlobClient(blobName);
        blob.delete();
        return true;
    }

    //GET FILE URL FROM CONTAINER images//
    public String getFileURL(String filename) {
        return blobContainerClient.getBlobClient(filename).getBlobUrl();
    }

    //GET AVATAR URL FROM CONTAINER images/accountId//
    //1. GET ACCOUNT FROM SESSION AND CHECK FOR EXIST FOLDER MATCH WITH ACCOUNT ID
    //2. IF NOT EXIST CREATE NEW FOLDER USING ACCOUNT ID AS NAME
    //3. REDIRECT TO CONTAINER images/accountId//
    //4. GET avatar.png FILE URL FROM CONTAINER images/accountId//
    public String getAvatar(String filename, HttpSession session) {
        //move container to subfolder images/accountId//
        Account sessionAccount = (Account) session.getAttribute("loginedUser");
        blobContainerClient = blobServiceClient.getBlobContainerClient("images/avatar/" + sessionAccount.getAccountId());
        BlobClient blob = blobContainerClient.getBlobClient(filename);
        if (blob.exists()) {
            return blobContainerClient.getBlobClient(filename).getBlobUrl();
        } else {
            return null;
        }
    }


    public String getURLFolder(String filename) {
        blobContainerClient = blobServiceClient.getBlobContainerClient("images/leave/");
        return blobContainerClient.getBlobClient(filename).getBlobUrl();
    }


}
