package org.matsim.viz.files.file;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import org.matsim.viz.error.InternalException;
import org.matsim.viz.files.entities.FileEntry;
import org.matsim.viz.files.entities.PendingFileTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class S3Repository extends LocalRepository {

    private static Logger logger = LoggerFactory.getLogger(S3Repository.class);
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final AmazonS3 s3;
    private final String bucketName;
    private final FileDAO fileDAO;

    public S3Repository(FileDAO fileDAO, AmazonS3 s3, String bucketName, String tmpUploadDirectory) {
        super(tmpUploadDirectory);
        this.fileDAO = fileDAO;
        this.s3 = s3;
        this.bucketName = bucketName;
    }

    @Override
    public List<FileEntry> addFiles(Collection<FileUpload> uploads) {
        return uploads.stream().map(this::addFile).collect(Collectors.toList());
    }

    @Override
    public FileEntry addFile(FileUpload item) {

        FileEntry entry = this.writeFileToDisk(item);
        entry.setPendingFileTransfer(new PendingFileTransfer(FileEntry.StorageType.S3, PendingFileTransfer.Status.Pending, entry));
        scheduleFileTransfer();
        return entry;
    }

    @Override
    public InputStream getFileStream(FileEntry fileEntry) {

        if (fileEntry.getStorageType() == FileEntry.StorageType.Local) {
            return super.getFileStream(fileEntry);
        }
        try {
            S3Object file = s3.getObject(bucketName, fileEntry.getPersistedFileName());
            return file.getObjectContent();
        } catch (AmazonClientException e) {
            logger.error("Error while fetching object from s3", e);
            throw new InternalException("Could not get file.");
        }
    }

    @Override
    public void removeFiles(Collection<FileEntry> entries) {
        entries.forEach(this::removeFile);
    }

    @Override
    public void removeFile(FileEntry entry) {

        if (entry.getStorageType() == FileEntry.StorageType.Local) {
            super.removeFile(entry);
        } else {
            try {
                s3.deleteObject(bucketName, entry.getPersistedFileName());
            } catch (AmazonClientException e) {
                logger.error("Error while deleting object in s3", e);
                throw new InternalException("Could not delete file");
            }
        }
    }

    private void scheduleFileTransfer() {
        // schedule with one second delay to make sure the database is updated first.
        // it is somewhat uncritical when files are transferred
        scheduler.schedule(this::transferPendingFiles, 2, TimeUnit.SECONDS);
    }

    private void transferPendingFiles() {

        logger.info("starting pending file transfer.");
        List<PendingFileTransfer> transfers = fileDAO.findAllPendingFileTransfersToS3();
        logger.info(transfers.size() + "file transfers found.");
        for (PendingFileTransfer transfer : transfers) {
            transferFile(transfer);
        }
    }

    private void transferFile(PendingFileTransfer transfer) {

        FileEntry entry = transfer.getFileEntry();
        logger.info("Starting file transfer s3 for file: " + entry.getPersistedFileName());

        try {
            transfer.setStatus(PendingFileTransfer.Status.Transferring);
            fileDAO.persistPendingFileTransfer(transfer);
            File file = uploadDirectory.resolve(entry.getPersistedFileName()).toFile();
            logger.info("Trying to connect to amazon s3");
            s3.putObject(this.bucketName, entry.getPersistedFileName(), file);
            logger.info("Upload succeeded. Deleting local file and pending file transfer. Also setting storage location so s3");
            entry.setStorageType(FileEntry.StorageType.S3);
            entry.setPendingFileTransfer(null); //deletes pending transfer
            fileDAO.persistFileEntry(entry);

            super.removeFile(entry);
        } catch (AmazonClientException e) {
            logger.error("S3 couldn't process request or could not connect to s3");
            transfer.setStatus(PendingFileTransfer.Status.Failed);
            fileDAO.persistPendingFileTransfer(transfer);
        } catch (Exception e) {
            logger.error("Unexpected exception!", e);
        }
    }
}
