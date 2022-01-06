package com.example.server;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileMonitor {
    public static final Path currentRelativePath = Paths.get("");
    public static final String path = currentRelativePath.toAbsolutePath().toString();

    @Bean
    public static void usingFileAlterationMonitor() throws IOException {
        FileAlterationObserver observer = new FileAlterationObserver(path + "/File/");
        observer.addListener(new FileAlterationListenerAdaptor(){

            @Override
            public void onFileChange(File file) {
                System.out.println("File modifiable: " + file.getName());
            }

            @Override
            public void onFileCreate(File file) {
                System.out.println("File created: " + file.getName());
            }

            @Override
            public void onFileDelete(File file) {
                System.out.println("File deleted: " + file.getName());
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
