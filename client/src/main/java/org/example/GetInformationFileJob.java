package org.example;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetInformationFileJob implements Job {

    public static boolean statusCreateFile = true;
    public static boolean statusDeleteFile = true;
    public static boolean statusModifiedFile = true;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        FileUtils.nameFileAndModifiedDate = FileUtils.getAttributesAllFile();
        File[] files = new File(FileUtils.path + "/src/main/resources/File/").listFiles();
        List<String> nameFileClient = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                try {
                    String nameFile = file.getName();
                    Path fileAttributes = Paths.get(FileUtils.path + "/src/main/resources/File/" + nameFile);
                    BasicFileAttributes attr = Files.readAttributes(fileAttributes, BasicFileAttributes.class);
                    if (!FileUtils.nameFileAndModifiedDate.containsKey(nameFile)) {
                        file.delete();
                        statusDeleteFile = false;
                    }
                    String lastModifiedDate = FileUtils.nameFileAndModifiedDate.get(nameFile);
                    if (lastModifiedDate != null && lastModifiedDate.compareToIgnoreCase(attr.lastModifiedTime().toString()) > 0) {
                        FileUtils.downloadFile(nameFile);
                        statusModifiedFile = false;
                    }
                    nameFileClient.add(nameFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (FileUtils.nameFileAndModifiedDate.size() > files.length) {
            for (Map.Entry m : FileUtils.nameFileAndModifiedDate.entrySet()) {
                if (!nameFileClient.contains(m.getKey().toString())) {
                    FileUtils.downloadFile(m.getKey().toString());
                    statusCreateFile = false;
                }
            }
        }
    }
}
