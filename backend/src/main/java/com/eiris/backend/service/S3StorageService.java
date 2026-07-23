package com.eiris.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final String bucketName;
    private final String endpointUrl;
    private final String projectId;

    public S3StorageService(
            @Value("${aws.s3.access-key}") String accessKey,
            @Value("${aws.s3.secret-key}") String secretKey,
            @Value("${aws.s3.endpoint}") String endpoint,
            @Value("${aws.s3.region}") String region,
            @Value("${aws.s3.bucket}") String bucketName) {
        
        this.bucketName = bucketName;
        this.endpointUrl = endpoint;
        
        // Extract project ID from endpoint to construct the public URL correctly
        // Format: https://[project-id].supabase.co/storage/v1/s3
        if (endpoint.contains(".")) {
            this.projectId = endpoint.split("://")[1].split("\\.")[0];
        } else {
            this.projectId = "";
        }

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(endpoint))
                // Supabase requires path style access
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    public String uploadImage(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // Use UUID to ensure unique filenames
        String fileName = "Admin_Products_Imagies/" + UUID.randomUUID().toString() + extension;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // Return the public URL for Supabase storage
        // Format: https://[project-id].supabase.co/storage/v1/object/public/[bucket]/[fileName]
        return "https://" + projectId + ".supabase.co/storage/v1/object/public/" + bucketName + "/" + fileName;
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty() || !imageUrl.contains("/" + bucketName + "/")) {
            return;
        }
        try {
            // Extract the key from the URL
            // URL format: https://[project-id].supabase.co/storage/v1/object/public/[bucket]/[key]
            String keyPrefix = "/" + bucketName + "/";
            int keyIndex = imageUrl.indexOf(keyPrefix);
            if (keyIndex != -1) {
                String key = imageUrl.substring(keyIndex + keyPrefix.length());
                
                software.amazon.awssdk.services.s3.model.DeleteObjectRequest deleteObjectRequest = software.amazon.awssdk.services.s3.model.DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();
                        
                s3Client.deleteObject(deleteObjectRequest);
                System.out.println("Deleted old image from S3: " + key);
            }
        } catch (Exception e) {
            System.err.println("Failed to delete image from S3: " + e.getMessage());
        }
    }
}
