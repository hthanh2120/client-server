package org.example;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.IOException;

public class FileMonitor {

    public static void usingFileAlterationMonitor() throws IOException {
        //get the file object
        FileAlterationObserver observer = new FileAlterationObserver(FileUtils.path + "/src/main/resources/File");
        observer.addListener(new FileAlterationListenerAdaptor(){

            @Override
            public void onFileChange(File file) {
                if(GetInformationFileJob.statusModifiedFile) {
                    String pathFile = FileUtils.path + file.getName();
                    FileUtils.uploadFile(FileUtils.urlUpload, pathFile);
                }
                GetInformationFileJob.statusModifiedFile = true;
            }

            @Override
            public void onFileCreate(File file) {
                if (GetInformationFileJob.statusCreateFile) {
                    String pathFile = file.getPath();
                    FileUtils.uploadFile(FileUtils.urlUpload, pathFile);
                }
                GetInformationFileJob.statusCreateFile = true;
                System.out.println("abcd");
            }

            @Override
            public void onFileDelete(File file) {
                if (GetInformationFileJob.statusDeleteFile) {
                    FileUtils.deleteFile(file.getName());
                }
                GetInformationFileJob.statusDeleteFile = true;
            }
        });
        FileAlterationMonitor monitor = new FileAlterationMonitor(500, observer);
        try {
            monitor.start();
        } catch(IOException e) {
            System.out.println(e.getMessage());
        } catch(InterruptedException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
