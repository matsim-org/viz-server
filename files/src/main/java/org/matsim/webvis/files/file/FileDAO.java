package org.matsim.webvis.files.file;

import org.matsim.webvis.database.PersistenceUnit;
import org.matsim.webvis.files.entities.*;

import java.util.List;

public class FileDAO extends DAO {

    public FileDAO(PersistenceUnit persistenceUnit) {
        super(persistenceUnit);
    }

    PendingFileTransfer persistPendingFileTransfer(PendingFileTransfer transfer) {
        return database.persist(transfer);
    }

    FileEntry persistFileEntry(FileEntry entry) {
        return database.persist(entry);
    }

    FileEntry findFileEntryById(String id) {

        QFileEntry fileEntry = QFileEntry.fileEntry;
        QPendingFileTransfer fileTransfer = QPendingFileTransfer.pendingFileTransfer;

        return database.executeQuery(query -> query.selectFrom(fileEntry)
                .where(fileEntry.id.eq(id))
                .leftJoin(fileEntry.pendingFileTransfer, fileTransfer).fetchJoin()
                .fetchOne()
        );
    }

    List<PendingFileTransfer> findAllPendingFileTransfersToS3() {

        QPendingFileTransfer transfer = QPendingFileTransfer.pendingFileTransfer;
        QFileEntry fileEntry = QFileEntry.fileEntry;

        return database.executeQuery(query -> query.selectFrom(transfer)
                .where(transfer.toStorage.eq(FileEntry.StorageType.S3)
                        .and(transfer.status.eq(PendingFileTransfer.Status.Pending)))
                .leftJoin(transfer.fileEntry, fileEntry).fetchJoin()
                .fetch());
    }

    public void removePendingFileTransfer(PendingFileTransfer transfer) {
        database.remove(transfer);
    }
}
