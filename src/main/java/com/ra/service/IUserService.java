package com.ra.service;


import com.ra.dto.request.UserLogin;
import com.ra.dto.request.UserRegister;
import com.ra.dto.response.JwtResponse;
import com.ra.model.Users;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IUserService {
	
	void register(UserRegister userRegister);
	
	JwtResponse login(UserLogin userLogin);
	
	List<Users> getAllUser();
	
	String handleLogout(Authentication authentication);
	
	JwtResponse handleRefreshToken(HttpServletRequest request, HttpServletResponse response);
	
}
