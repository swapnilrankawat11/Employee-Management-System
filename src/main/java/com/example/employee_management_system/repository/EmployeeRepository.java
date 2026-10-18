package com.example.employee_management_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employee_management_system.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	boolean existsByDepartmentId(Long departmentId);

	boolean existsByEmail(String email);

	Optional<Employee> findByEmail(String email);
}
