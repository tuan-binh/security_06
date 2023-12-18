package com.ra.service;


import com.ra.dto.request.UserLogin;
import com.ra.dto.request.UserRegister;
import com.ra.dto.response.JwtResponse;

public interface IUserService {
	
	void register(UserRegister userRegister);
	
	JwtResponse login(UserLogin userLogin);
	
}
