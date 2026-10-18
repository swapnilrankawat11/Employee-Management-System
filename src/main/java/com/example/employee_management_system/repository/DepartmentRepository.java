package com.example.employee_management_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employee_management_system.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
	boolean existsByName(String name);

	Optional<Department> findByName(String name);
}
