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
import jakarta.validation.Valid;
import com.example.employee_management_system.entity.Employee;
import com.example.employee_management_system.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
	private final EmployeeService service;

	public EmployeeController(EmployeeService service) {
		this.service = service;
	}

	@GetMapping
	public List<Employee> getAllEmployees() {
		return service.getAllEmployees();
	}

	@GetMapping("/{id}")
	public Employee getEmployeeById(@PathVariable Long id) {
		return service.getEmployeeById(id);
	}

	@PostMapping
	public Employee createEmployee(@Valid @RequestBody Employee emp) {
		return service.createEmployee(emp);
	}

	@PutMapping("/{id}")
	public Employee updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee emp) {
		return service.updateEmployee(id, emp);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
		service.deleteEmployee(id);
		return ResponseEntity.noContent().build();
	}
}
