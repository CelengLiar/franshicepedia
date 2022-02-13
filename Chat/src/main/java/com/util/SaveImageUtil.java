package com.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SaveImageUtil {
	
	private static final String ROOT_DIR = "images/";
	
	public static void saveImage( String id,String name, MultipartFile multipartFile) throws IOException {
		Path uploadPath = Paths.get(ROOT_DIR + id);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		
		try(InputStream inputStream = multipartFile.getInputStream()){
			Path filePath = uploadPath.resolve(name);
			Files.copy(inputStream, filePath,StandardCopyOption.REPLACE_EXISTING);
		}catch (IOException e) {
			// TODO: handle exception
			throw new IOException("gagal save: " + name,e);
		}
	}
	
	public static Resource getImage(String id, String name) {
		try {
			Path file = Paths.get(ROOT_DIR+id).resolve(name);
			Resource resource =  new UrlResource(file.toUri());
			
			if(resource.exists() )
				return resource;
			throw new RuntimeException("ERROR: NOT FOUND" );
		} catch (IOException e) {
			// TODO: handle exception
			throw new RuntimeException("ERROR: "+ e.getLocalizedMessage());
		}
	}

}
