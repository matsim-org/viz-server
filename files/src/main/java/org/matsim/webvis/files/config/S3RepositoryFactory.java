package org.matsim.webvis.files.config;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.matsim.webvis.database.PersistenceUnit;
import org.matsim.webvis.files.file.FileDAO;
import org.matsim.webvis.files.file.Repository;
import org.matsim.webvis.files.file.S3Repository;

@Getter
@JsonTypeName("s3")
public class S3RepositoryFactory implements RepositoryFactory {

    private String bucketName;
    private String tmpUploadDirectory;
    private String region;

    @Override
    public Repository createRepository(PersistenceUnit persistenceUnit) {

        AmazonS3 client = AmazonS3ClientBuilder.standard().withRegion(region)
                .withCredentials(new ProfileCredentialsProvider()).build();

        return new S3Repository(new FileDAO(persistenceUnit), client, bucketName, tmpUploadDirectory);
    }
}
