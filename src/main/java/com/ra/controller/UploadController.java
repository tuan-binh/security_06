package com.ra.controller;

import com.ra.model.Image;
import com.ra.service.IUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/upload")
public class UploadController {
	
	@Autowired
	private IUploadService uploadService;
	
	@GetMapping
	public ResponseEntity<List<Image>> getAllImages() {
		return new ResponseEntity<>(uploadService.findAll(), HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<List<Image>> addNewImage(@ModelAttribute("images") List<MultipartFile> images) {
		return new ResponseEntity<>(uploadService.addNewImage(images), HttpStatus.CREATED);
	}
	
	@PostMapping("/single")
	public ResponseEntity<Image> addSingleImage(@ModelAttribute("image") MultipartFile image) {
		return new ResponseEntity<>(uploadService.addSingleImage(image), HttpStatus.CREATED);
	}
	
}
