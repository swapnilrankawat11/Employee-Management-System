package com.example.employee_management_system.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.employee_management_system.entity.Department;
import com.example.employee_management_system.service.DepartmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
	private final DepartmentService service;

	public DepartmentController(DepartmentService service) {
		this.service = service;
	}

	@GetMapping
	public List<Department> getAllDepartments() {
		return service.getAllDepartments();
	}

	@GetMapping("/{id}")
	public Department getDepartmentById(@PathVariable Long id) {
		return service.getDepartmentById(id);
	}

	@PostMapping
	public Department createDepartment(@Valid @RequestBody Department dept) {
		return service.createDepartment(dept);
	}

	@PutMapping("/{id}")
	public Department updateDepartment(@PathVariable Long id, @Valid @RequestBody Department dept) {
		return service.updateDepartment(id, dept);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
		service.deleteDepartment(id);
		return ResponseEntity.noContent().build();
	}
}
