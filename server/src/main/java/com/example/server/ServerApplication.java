package com.example.server;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@SpringBootApplication
@Controller
public class ServerApplication {
	public static final Path currentRelativePath = Paths.get("");
	public static final String path = currentRelativePath.toAbsolutePath().toString();

	@RequestMapping(path = "/uploadFileServer", method = POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<String> saveFile(@RequestPart MultipartFile fileName, HttpServletRequest request) {
		if (!fileName.getName().equals("")) {
			try {
				String orgName = fileName.getOriginalFilename();
				File dest = new File(path + "/File/"+orgName);
				fileName.transferTo(dest);
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<>("NOT OK",HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<>("NOT OK",HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>("OK",HttpStatus.OK);
	}

	@RequestMapping(path = "/informationFile", method = GET)
	public ResponseEntity<HashMap<String, String>> getFileInformation() {
		HashMap<String, String> results = new HashMap<>();
		File[] files = new File(path + "/File/").listFiles();

		for (File file : files) {
			if (file.isFile()) {
				try {
					Path fileAttributes = Paths.get(path +"/File/"+ file.getName());
					BasicFileAttributes attr = Files.readAttributes(fileAttributes, BasicFileAttributes.class);
					String lastModifiedDate = attr.lastModifiedTime().toString().substring(0, attr.lastModifiedTime().toString().lastIndexOf("."));
					results.put(file.getName(), lastModifiedDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return new ResponseEntity<>(results, HttpStatus.OK);
	}

	@RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String fileName,HttpServletRequest request) throws IOException {
		HttpHeaders responseHeader = new HttpHeaders();
		try {
			File file = ResourceUtils.getFile("classpath:File/"+fileName);
			byte[] data = FileUtils.readFileToByteArray(file);
			responseHeader.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			responseHeader.set("Content-disposition", "attachment; filename=" + file.getName());
			responseHeader.setContentLength(data.length);
			InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
			InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
			return new ResponseEntity<InputStreamResource>(inputStreamResource, responseHeader, HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<InputStreamResource>(null, responseHeader, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/deleteFile", method = RequestMethod.GET)
	public ResponseEntity<String> deleteFile(@RequestParam String fileName) {
		HttpHeaders responseHeader = new HttpHeaders();
		try {
			File file = new File(path  +"/File/"+ fileName);
			boolean s = file.delete();
			System.out.println(s);
			return new ResponseEntity<>("OK", responseHeader, HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>("NOT OK", responseHeader, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		Class<?>[] sources = {ServerApplication.class, FileMonitor.class};
		SpringApplication.run(sources, args);
	}

}
