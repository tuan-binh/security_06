package com.ra.controller;

import com.ra.dto.request.UserLogin;
import com.ra.dto.request.UserRegister;
import com.ra.dto.response.JwtResponse;
import com.ra.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
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
	
	@PostMapping("/refreshToken")
	public ResponseEntity<JwtResponse> handleRefreshToken(HttpServletRequest request, HttpServletResponse response) {
		return new ResponseEntity<>(userService.handleRefreshToken(request, response), HttpStatus.OK);
	}
	
	@DeleteMapping("/logout")
	public ResponseEntity<String> handleLogout(Authentication authentication) {
		return new ResponseEntity<>(userService.handleLogout(authentication), HttpStatus.OK);
	}
	
}
