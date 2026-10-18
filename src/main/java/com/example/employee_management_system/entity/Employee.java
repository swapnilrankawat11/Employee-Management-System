package com.example.employee_management_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Entity
@Table(name = "employees")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Employee name is required.")
	@Column(nullable = false, length = 100)
	private String name;

	@NotBlank(message = "Email is required.")
	@Email(message = "Enter a valid email address.")
	@Column(nullable = false, unique = true, length = 255)
	private String email;

	@NotNull(message = "Salary is required.")
	@Positive(message = "Salary must be postive.")
	@Column(nullable = false)
	private BigDecimal salary;

	@NotNull(message = "Department is required.")
	@ManyToOne(optional = false)
	@JoinColumn(name = "department_id", nullable = false)
	private Department department;

	public Employee() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

}
