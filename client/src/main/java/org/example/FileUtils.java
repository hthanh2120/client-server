package org.example;

import com.google.gson.Gson;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class FileUtils {

    public static HashMap<String, String> nameFileAndModifiedDate = new HashMap<>();
    public final static String urlDownload = "http://localhost:8080/downloadFile?fileName=";
    public final static String urlUpload = "http://localhost:8080/uploadFileServer";
    public final static String urlDeleteFile = "http://localhost:8080/deleteFile?fileName=";
    public final static String urlAttributesAllFile = "http://localhost:8080/informationFile";
    public static final Path currentRelativePath = Paths.get("");
    public static final String path = currentRelativePath.toAbsolutePath().toString();

    public static File downloadFile(String fileNameDirectory) {
        try {
            String fileNameDirectoryEncode = URLEncoder.encode(fileNameDirectory, StandardCharsets.UTF_8.name());

            URL url = new URL(urlDownload + fileNameDirectoryEncode);

            URLConnection urlConnection = url.openConnection();

            urlConnection.setDoInput(true);

            urlConnection.setDoOutput(true);

            int fileLength = urlConnection.getContentLength();

            String filePathName = urlConnection.getURL().getFile();

            String result = URLDecoder.decode(filePathName, StandardCharsets.UTF_8.name());

            String fileName = result.substring(result.lastIndexOf("=") + 1);

            String pathFile = path + "/src/main/resources/File/" + fileName;

            File file = new File(pathFile);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            InputStream inputStream = urlConnection.getInputStream();

            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            OutputStream outputStream = new FileOutputStream(file);

            int size;
            // Define an integer to accumulate the length of the file currently read
            int len = 0;
            // Define byte array objects to load data blocks from the input buffer
            byte[] buf = new byte[1024];
            // Read 1024 bytes of file content from the input buffer to the buf object at one time, and assign the read size to the size variable. When the read is finished, size=-1, and end the cycle reading.
            while ((size = bufferedInputStream.read(buf)) != -1) {
                // Accumulate the size of each read file
                len += size;
                // Write data to the output stream
                outputStream.write(buf, 0, size);
                // Percentage of current file downloads printed
                System.out.println("Download progress:" + len * 100 / fileLength + "%\n");
            }
            // Close the output stream
            outputStream.close();
            // Close the input buffer
            bufferedInputStream.close();
            // Close the input stream
            inputStream.close();

            // Return file object
            return file;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteFile(String fileName) {
        try {
            URL url = new URL(urlDeleteFile + fileName);
            URLConnection urlcon = url.openConnection();
            InputStream stream = urlcon.getInputStream();
            int i;
            StringBuilder result = new StringBuilder();
            while ((i = stream.read()) != -1) {
                result.append((char) i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static HashMap<String, String> getAttributesAllFile() {
        try {
            URL url = new URL(urlAttributesAllFile);
            URLConnection urlcon = url.openConnection();
            InputStream stream = urlcon.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(stream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = "", newLine;

            while ((newLine = bufferedReader.readLine()) != null) {
                str += newLine + "\n";
            }
            inputStreamReader.close();
            HashMap<String, String> map = new Gson().fromJson(str, HashMap.class);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String uploadFile(String actionURL, String filePaths) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            File filePath = new File(filePaths);
            Resource resource = new FileSystemResource(filePath);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("fileName", resource);
            System.out.println();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(actionURL, new HttpEntity<MultiValueMap<String, Object>>(body), String.class);

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
