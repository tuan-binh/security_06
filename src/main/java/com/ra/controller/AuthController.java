package com.ra.controller;

import com.ra.dto.request.UserLogin;
import com.ra.dto.request.UserRegister;
import com.ra.dto.response.JwtResponse;
import com.ra.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private IUserService userService;
	
	@PostMapping("/login")
	public ResponseEntity<JwtResponse> handleLogin(@RequestBody UserLogin userLogin) {
		return new ResponseEntity<>(userService.login(userLogin), HttpStatus.OK);
	}
	
	@PostMapping("/register")
	public ResponseEntity<String> handleRegister(@RequestBody UserRegister userRegister) {
		userService.register(userRegister);
		return new ResponseEntity<>("success",HttpStatus.CREATED);
	}
	
}
