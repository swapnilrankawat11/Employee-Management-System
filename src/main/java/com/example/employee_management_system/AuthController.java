package com.example.employee_management_system;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AppUserService service;

	public AuthController(AppUserService service) {
		this.service = service;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
		AppUser user = new AppUser();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		user.setRole("USER");

		AppUser savedUser = service.registerUser(user);

		return ResponseEntity.status(HttpStatus.CREATED).body(java.util.Map.of("message", "Registration successful",
				"id", savedUser.getId(), "username", savedUser.getUsername(), "email", savedUser.getEmail()));
	}
}
