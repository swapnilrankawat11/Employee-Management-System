package com.example.employee_management_system.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.employee_management_system.entity.Department;
import com.example.employee_management_system.entity.Employee;
import com.example.employee_management_system.repository.DepartmentRepository;
import com.example.employee_management_system.repository.EmployeeRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class EmployeeIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	private Long departmentId;

	@BeforeEach
	void setUp() {
		employeeRepository.deleteAll();
		departmentRepository.deleteAll();

		Department department = new Department();
		department.setName("IT");
		department.setDescription("Integration test department");

		Department savedDepartment = departmentRepository.saveAndFlush(department);

		departmentId = savedDepartment.getId();
	}

	@Test
	void createEmployee_savesEmployeeToDatabase() throws Exception {

		mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content("""
				{
				    "name": "Swapnil",
				    "email": "swapnil@example.com",
				    "salary": 45000.00,
				    "department": {
				        "id": %d
				    }
				}
				""".formatted(departmentId)))

				.andExpect(status().isOk()).andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.name").value("Swapnil"))
				.andExpect(jsonPath("$.email").value("swapnil@example.com"))
				.andExpect(jsonPath("$.salary").value(45000.00))
				.andExpect(jsonPath("$.department.id").value(departmentId.intValue()))
				.andExpect(jsonPath("$.department.name").value("IT"));

		Employee savedEmployee = employeeRepository.findByEmail("swapnil@example.com")
				.orElseThrow(() -> new AssertionError("Employee was not saved"));

		assertTrue(savedEmployee.getId() > 0);
		assertEquals("Swapnil", savedEmployee.getName());
		assertEquals("swapnil@example.com", savedEmployee.getEmail());

		assertEquals(0, savedEmployee.getSalary().compareTo(new BigDecimal("45000.00")));

		assertNotNull(savedEmployee.getDepartment());
		assertEquals(departmentId, savedEmployee.getDepartment().getId());
		assertEquals("IT", savedEmployee.getDepartment().getName());
	}

	@Test
	void getAllEmployees_returnsEmployeesFromDatabase() throws Exception {

		Employee employee = createTestEmployee("Swapnil", "swapnil@example.com", "45000.00");

		employeeRepository.saveAndFlush(employee);

		mockMvc.perform(get("/api/employees")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(employee.getId().intValue()))
				.andExpect(jsonPath("$[0].name").value("Swapnil"))
				.andExpect(jsonPath("$[0].email").value("swapnil@example.com"))
				.andExpect(jsonPath("$[0].salary").value(45000.00))
				.andExpect(jsonPath("$[0].department.id").value(departmentId.intValue()))
				.andExpect(jsonPath("$[0].department.name").value("IT"));
	}

	@Test
	void getEmployeeById_returnsEmployeeFromDatabase() throws Exception {

		Employee employee = createTestEmployee("Swapnil", "swapnil@example.com", "45000.00");

		Employee savedEmployee = employeeRepository.saveAndFlush(employee);

		mockMvc.perform(get("/api/employees/{id}", savedEmployee.getId())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(savedEmployee.getId().intValue()))
				.andExpect(jsonPath("$.name").value("Swapnil"))
				.andExpect(jsonPath("$.email").value("swapnil@example.com"))
				.andExpect(jsonPath("$.salary").value(45000.00))
				.andExpect(jsonPath("$.department.id").value(departmentId.intValue()))
				.andExpect(jsonPath("$.department.name").value("IT"));
	}

	@Test
	void updateEmployee_updatesDatabaseRecord() throws Exception {

		Employee employee = createTestEmployee("Swapnil", "swapnil@example.com", "45000.00");

		Employee savedEmployee = employeeRepository.saveAndFlush(employee);

		mockMvc.perform(
				put("/api/employees/{id}", savedEmployee.getId()).contentType(MediaType.APPLICATION_JSON).content("""
						{
						    "name": "Swapnil Updated",
						    "email": "swapnil.updated@example.com",
						    "salary": 55000.00,
						    "department": {
						        "id": %d
						    }
						}
						""".formatted(departmentId)))

				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(savedEmployee.getId().intValue()))
				.andExpect(jsonPath("$.name").value("Swapnil Updated"))
				.andExpect(jsonPath("$.email").value("swapnil.updated@example.com"))
				.andExpect(jsonPath("$.salary").value(55000.00))
				.andExpect(jsonPath("$.department.id").value(departmentId.intValue()))
				.andExpect(jsonPath("$.department.name").value("IT"));

		Employee updatedEmployee = employeeRepository.findById(savedEmployee.getId())
				.orElseThrow(() -> new AssertionError("Updated employee not found"));

		assertEquals("Swapnil Updated", updatedEmployee.getName());
		assertEquals("swapnil.updated@example.com", updatedEmployee.getEmail());

		assertEquals(0, updatedEmployee.getSalary().compareTo(new BigDecimal("55000.00")));

		assertEquals(departmentId, updatedEmployee.getDepartment().getId());
	}

	@Test
	void deleteEmployee_removesEmployeeFromDatabase() throws Exception {

		Employee employee = createTestEmployee("Swapnil", "swapnil@example.com", "45000.00");

		Employee savedEmployee = employeeRepository.saveAndFlush(employee);
		Long employeeId = savedEmployee.getId();

		mockMvc.perform(delete("/api/employees/{id}", employeeId)).andExpect(status().isNoContent());

		Optional<Employee> deletedEmployee = employeeRepository.findById(employeeId);

		assertFalse(deletedEmployee.isPresent());
	}

	private Employee createTestEmployee(String name, String email, String salary) {

		Employee employee = new Employee();
		employee.setName(name);
		employee.setEmail(email);
		employee.setSalary(new BigDecimal(salary));

		Department department = departmentRepository.findById(departmentId)
				.orElseThrow(() -> new AssertionError("Test department not found"));

		employee.setDepartment(department);

		return employee;
	}
}
