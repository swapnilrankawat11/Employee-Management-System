package com.example.employee_management_system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.employee_management_system.entity.Department;
import com.example.employee_management_system.exception.ResourceNotFoundException;
import com.example.employee_management_system.repository.DepartmentRepository;
import com.example.employee_management_system.repository.EmployeeRepository;

@Service
public class DepartmentService {
	private final DepartmentRepository repo;
	private final EmployeeRepository empRepo;

	public DepartmentService(DepartmentRepository repo, EmployeeRepository empRepo) {
		this.repo = repo;
		this.empRepo = empRepo;
	}

	public List<Department> getAllDepartments() {
		return repo.findAll();
	}

	public Department createDepartment(Department dept) {
		if (repo.existsByName(dept.getName())) {
			throw new IllegalStateException("A department with this name already exists.");
		}
		return repo.save(dept);
	}

	public Department getDepartmentById(Long id) {
		Optional<Department> dept = repo.findById(id);
		return dept.orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
	}

	public Department updateDepartment(Long id, Department dept) {
		Department existingDept = repo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
		if (!existingDept.getName().equalsIgnoreCase(dept.getName()) && repo.existsByName(dept.getName())) {
			throw new IllegalStateException("A department with this name already exists.");
		}
		existingDept.setName(dept.getName());
		existingDept.setDescription(dept.getDescription());
		return repo.save(existingDept);
	}

	public void deleteDepartment(Long id) {
		repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
		if (empRepo.existsByDepartmentId(id)) {
			throw new IllegalStateException("Can't delete a department having employees assigned!");
		}
		repo.deleteById(id);
	}
}
