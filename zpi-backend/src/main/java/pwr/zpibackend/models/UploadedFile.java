package pwr.zpibackend.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "uploaded_files")
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    /*
        org.hibernate.tool.schema.spi.CommandAcceptanceException: Error executing DDL "
        alter table if exists uploaded_files
           alter column file_data set data type oid" via JDBC [BŁĄD: kolumna "file_data" nie może być rzutowana automatycznie na typ oid
      Hint: Być może trzeba wskazać "USING file_data::oid".]
        at org.hibernate.tool.schema.internal.exec.GenerationTargetToDatabase.accept(GenerationTargetToDatabase.java:92) ~[hibernate-core-6.2.7.Final.jar:6.2.7.Final]
        at org.hibernate.tool.schema.internal.AbstractSchemaMigrator.applySqlString(AbstractSchemaMigrator.java:574) ~[hibernate-core-6.2.7.Final.jar:6.2.7.Final]
        at org.hibernate.tool.schema.internal.AbstractSchemaMigrator.applySqlStrings(AbstractSchemaMigrator.java:514) ~[hibernate-core-6.2.7.Final.jar:6.2.7.Final]
        at org.hibernate.tool.schema.internal.AbstractSchemaMigrator.migrateTable(AbstractSchemaMigrator.java:333) ~[hibernate-core-6.2.7.Final.jar:6.2.7.Final]
        at org.hibernate.tool.schema.internal.GroupedSchemaMigratorImpl.performTablesMigration(GroupedSchemaMigratorImpl.java:84) ~[hibernate-core-6.2.7.Final.jar:6.2.7.Final]
        at org.hibernate.tool.schema.internal.AbstractSchemaMigrator.performMigration(AbstractSchemaMigrator.java:232) ~[hibernate-core-6.2.7.Final.jar:6.2.7.Final]
        at org.hibernate.tool.schema.internal.AbstractSchemaMigrator.doMigration(AbstractSchemaMigrator.java:117) ~[hibernate-core-6.2.7.Final.jar:6.2.7.Final]
        at org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator.performDatabaseAction(SchemaManagementToolCoordinator.java:284) ~[hibernate-core-6.2.7.Final.jar:6.2.7.Final]
        at org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator.lambda$process$5(SchemaManagementToolCoordinator.java:143) ~[hibernate-core-6.2.7.Final.jar:6.2.7.Final]

        ....

        Caused by: org.postgresql.util.PSQLException: BŁĄD: kolumna "file_data" nie może być rzutowana automatycznie na typ oid
        Hint: Być może trzeba wskazać "USING file_data::oid".
     */
    //  @Lob //   cause of the error above - Large Object
    private byte[] fileData;

    public UploadedFile(){ }

    public UploadedFile(byte[] fileData) {
        this.fileName = "default";
        this.fileData = fileData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
}
