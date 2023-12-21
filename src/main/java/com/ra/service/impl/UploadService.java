package com.ra.service.impl;

import com.ra.model.Image;
import com.ra.repository.IUploadRepository;
import com.ra.service.IUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class UploadService implements IUploadService {
	
	@Autowired
	private IUploadRepository uploadRepository;
	@Value("${path-upload}")
	private String pathUpload;
	
	@Value("${server.port}")
	private Long port;
	
	@Override
	public List<Image> findAll() {
		return uploadRepository.findAll();
	}
	
	@Override
	public List<Image> addNewImage(List<MultipartFile> images) {
		
		List<String> fileNames = images.stream().map(MultipartFile::getOriginalFilename).toList();
		
		images.forEach(item -> {
			try {
				FileCopyUtils.copy(item.getBytes(), new File(pathUpload + item.getOriginalFilename()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		
		List<Image> results = fileNames.stream().map(item -> Image.builder().url(item).build()).toList();
		uploadRepository.saveAll(results);
		return uploadRepository.findAll();
	}
	
	@Override
	public Image addSingleImage(MultipartFile image) {
		String fileName = image.getOriginalFilename();
		
		try {
			FileCopyUtils.copy(image.getBytes(), new File(pathUpload + fileName));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		
		Image image1 = Image.builder().url("http://localhost:" + port + "/" + fileName).build();
		
		return uploadRepository.save(image1);
	}
}
