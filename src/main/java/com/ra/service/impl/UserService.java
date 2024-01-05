package com.ra.service.impl;

import com.ra.dto.request.UserLogin;
import com.ra.dto.request.UserRegister;
import com.ra.dto.response.JwtResponse;
import com.ra.model.RoleName;
import com.ra.model.Roles;
import com.ra.model.Users;
import com.ra.repository.IUserRepository;
import com.ra.security.jwt.JwtProvider;
import com.ra.security.jwt.JwtTokenFilter;
import com.ra.security.user_principal.UserDetailService;
import com.ra.security.user_principal.UserPrincipal;
import com.ra.service.IRoleService;
import com.ra.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {
	
	@Value("${jwt.expired}")
	private Long EXPIRED;
	
	private final Logger logger = LoggerFactory.getLogger(JwtProvider.class);
	
	@Autowired
	private IUserRepository userRepository;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationProvider authenticationProvider;
	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private JwtTokenFilter jwtTokenFilter;
	@Autowired
	private UserDetailService userDetailService;
	
	@Override
	public void register(UserRegister userRegister) {
		if (userRepository.existsByUsername(userRegister.getUsername())) {
			throw new RuntimeException("username is exists");
		}
		Set<Roles> roles = new HashSet<>();
		
		// Nếu không có quyền được truyền lên, mặc định là role user
		if (userRegister.getRoles() == null || userRegister.getRoles().isEmpty()) {
			roles.add(roleService.findByRoleName(RoleName.ROLE_USER));
		} else {
			// Xác định quyền dựa trên danh sách quyền được truyền lên
			userRegister.getRoles().forEach(role -> {
				switch (role) {
					case "admin":
						roles.add(roleService.findByRoleName(RoleName.ROLE_ADMIN));
					case "user":
						roles.add(roleService.findByRoleName(RoleName.ROLE_USER));
						break;
					default:
						throw new RuntimeException("role not found");
				}
			});
		}
		userRepository.save(Users.builder()
				  .fullName(userRegister.getFullName())
				  .username(userRegister.getUsername())
				  .password(passwordEncoder.encode(userRegister.getPassword()))
				  .roles(roles)
				  .build());
	}
	
	@Override
	public JwtResponse login(UserLogin userLogin) {
		Authentication authentication;
		try {
			authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getUsername(), userLogin.getPassword()));
		} catch (AuthenticationException e) {
			throw new RuntimeException("Username or Password is incorrect 11312321");
		}
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		
		Users users = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new RuntimeException("user not found"));
		String refreshToken;
		if (users.getRefreshToken() == null || users.getRefreshToken().isEmpty()) {
			refreshToken = jwtProvider.generateRefreshToken(userPrincipal);
			
		} else {
			if (!jwtProvider.isTokenExpired(users.getRefreshToken())) {
				refreshToken = users.getRefreshToken();
			} else {
				refreshToken = jwtProvider.generateRefreshToken(userPrincipal);
			}
		}
		// lưu refresh token vào database
		users.setRefreshToken(refreshToken);
		userRepository.save(users);
		// thực hiện trả về cho người dùng
		return JwtResponse.builder()
				  .accessToken(jwtProvider.generateToken(userPrincipal))
				  .refreshToken(refreshToken)
				  .expired(EXPIRED)
				  .fullName(userPrincipal.getFullName())
				  .username(userPrincipal.getUsername())
				  .roles(userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
				  .build();
	}
	
	@Override
	public List<Users> getAllUser() {
		return userRepository.findAll();
	}
	
	@Override
	public String handleLogout(Authentication authentication) {
		return null;
	}
	
	@Override
	public JwtResponse handleRefreshToken(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = jwtTokenFilter.getTokenFromRequest(request);
		if (refreshToken != null || jwtProvider.validateToken(refreshToken)) {
			String username = jwtProvider.getUsernameFromToken(refreshToken);
			
			UserPrincipal userPrincipal = (UserPrincipal) userDetailService.loadUserByUsername(username);
			if (jwtProvider.isTokenValid(refreshToken, userPrincipal) && !jwtProvider.isTokenExpired(refreshToken)) {
				String accessToken = jwtProvider.generateToken(userPrincipal);
				return JwtResponse.builder()
						  .accessToken(accessToken)
						  .refreshToken(refreshToken)
						  .expired(EXPIRED)
						  .fullName(userPrincipal.getFullName())
						  .username(userPrincipal.getUsername())
						  .roles(userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
						  .build();
			} else {
				throw new RuntimeException("can't generate token");
			}
		} else {
			throw new RuntimeException("Un Authentication");
		}
	}
}
