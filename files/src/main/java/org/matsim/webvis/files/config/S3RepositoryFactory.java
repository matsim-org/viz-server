package org.matsim.webvis.files.config;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.matsim.webvis.database.PersistenceUnit;
import org.matsim.webvis.files.file.FileDAO;
import org.matsim.webvis.files.file.Repository;
import org.matsim.webvis.files.file.S3Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Getter
@JsonTypeName("s3")
public class S3RepositoryFactory implements RepositoryFactory {

    private final Logger logger = LoggerFactory.getLogger(S3RepositoryFactory.class);
    private String bucketName;
    private String tmpUploadDirectory;
    private String region;

    @Override
    public Repository createRepository(PersistenceUnit persistenceUnit) {

        AmazonS3 client = AmazonS3ClientBuilder.standard().withRegion(region)
                .withCredentials(new DefaultAWSCredentialsProviderChain()).build();
        checkPermissions(client, bucketName);

        logger.info("Creating S3 repository at bucketName: " + bucketName);
        return new S3Repository(new FileDAO(persistenceUnit), client, bucketName, tmpUploadDirectory);
    }

    private void checkPermissions(AmazonS3 s3, String bucketName) {

        logger.info("Checking permissions on s3 bucket: " + bucketName);
        String objectKey = UUID.randomUUID().toString();
        String content = "connection test";
        try {
            s3.putObject(bucketName, objectKey, content);
        } catch (SdkClientException e) {
            logger.error("S3 connection Test failed. Could not write object");
            throw new RuntimeException(e);
        }
        try {
            s3.getObject(bucketName, objectKey);
        } catch (SdkClientException e) {
            logger.error("S3 connection test failed. Could not read object.");
            throw new RuntimeException(e);
        }
        try {
            s3.deleteObject(bucketName, objectKey);
        } catch (SdkClientException e) {
            logger.error("S3 connection test failed. Could not delete object.");
            throw new RuntimeException(e);
        }
        logger.info("Permission check successful. Client was able to read, write and delete objects.");
    }
}
