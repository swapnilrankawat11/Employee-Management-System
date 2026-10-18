package com.example.employee_management_system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.example.employee_management_system.entity.Department;
import com.example.employee_management_system.repository.DepartmentRepository;
import com.example.employee_management_system.entity.Employee;
import com.example.employee_management_system.exception.ResourceNotFoundException;
import com.example.employee_management_system.repository.EmployeeRepository;

@Service
public class EmployeeService {
	private final EmployeeRepository repo;
	private final DepartmentRepository departmentRepo;

	public EmployeeService(EmployeeRepository repo, DepartmentRepository departmentRepo) {
		this.repo = repo;
		this.departmentRepo = departmentRepo;
	}

	public List<Employee> getAllEmployees() {
		return repo.findAll();
	}

	public Employee getEmployeeById(Long id) {
		Optional<Employee> emp = repo.findById(id);
		return emp.orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
	}

	public Employee createEmployee(Employee emp) {
		if (repo.existsByEmail(emp.getEmail())) {
			throw new IllegalStateException("Email already exists!");
		}

		Department department = departmentRepo.findById(emp.getDepartment().getId())
				.orElseThrow(() -> new ResourceNotFoundException("Department not found"));

		emp.setDepartment(department);

		return repo.save(emp);
	}

	public Employee updateEmployee(Long id, Employee emp) {
		Employee existingEmp = repo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

		if (!existingEmp.getEmail().equalsIgnoreCase(emp.getEmail()) && repo.existsByEmail(emp.getEmail())) {
			throw new IllegalStateException("Email already exists!");
		}

		Department department = departmentRepo.findById(emp.getDepartment().getId())
				.orElseThrow(() -> new ResourceNotFoundException("Department not found"));

		existingEmp.setName(emp.getName());
		existingEmp.setEmail(emp.getEmail());
		existingEmp.setSalary(emp.getSalary());
		existingEmp.setDepartment(department);

		return repo.save(existingEmp);
	}

	public void deleteEmployee(Long id) {
		repo.findById(id).orElseThrow(() -> new RuntimeException("Employee deosn't exists!"));
		repo.deleteById(id);
	}
}
