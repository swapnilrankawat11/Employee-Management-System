package com.example.employee_management_system;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserService {
	private final AppUserRepository repo;
	private final PasswordEncoder enc;

	public AppUserService(AppUserRepository repo, PasswordEncoder enc) {
		this.repo = repo;
		this.enc = enc;
	}

	@Transactional
	public AppUser registerUser(AppUser user) {
		if (repo.existsByUsername(user.getUsername())) {
			throw new IllegalStateException("Username already exists!");
		} else if (repo.existsByEmail(user.getEmail())) {
			throw new IllegalStateException("Email already in use!");
		}

		user.setPassword(enc.encode(user.getPassword()));

		if (user.getRole() == null || user.getRole().isBlank()) {
			user.setRole("USER");
		}
		return repo.save(user);
	}
}
