
package com.example.employee_management_system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.employee_management_system.entity.Department;
import com.example.employee_management_system.entity.Employee;
import com.example.employee_management_system.service.EmployeeService;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EmployeeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EmployeeService service;

	private Department createDepartment() {
		Department dept = new Department();
		dept.setId(1L);
		dept.setName("IT");
		return dept;
	}

	private Employee createEmployee() {
		return createEmployee("Swapnil", "swapnil@example.com", "45000.00");
	}

	private Employee createEmployee(String name, String email, String salary) {
		Employee emp = new Employee();
		emp.setId(1L);
		emp.setName(name);
		emp.setEmail(email);
		emp.setSalary(new BigDecimal(salary));
		emp.setDepartment(createDepartment());
		return emp;
	}

	@Test
	void getAllEmployees_returnsEmployees() throws Exception {
		Employee emp = createEmployee();

		when(service.getAllEmployees()).thenReturn(List.of(emp));

		mockMvc.perform(get("/api/employees")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Swapnil"))
				.andExpect(jsonPath("$[0].email").value("swapnil@example.com"))
				.andExpect(jsonPath("$[0].salary").value(45000.00)).andExpect(jsonPath("$[0].department.id").value(1))
				.andExpect(jsonPath("$[0].department.name").value("IT"));
	}

	@Test
	void getEmployeeById_returnsEmployee() throws Exception {
		Employee emp = createEmployee();

		when(service.getEmployeeById(1L)).thenReturn(emp);

		mockMvc.perform(get("/api/employees/{id}", 1L)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Swapnil"))
				.andExpect(jsonPath("$.email").value("swapnil@example.com"))
				.andExpect(jsonPath("$.salary").value(45000.00)).andExpect(jsonPath("$.department.id").value(1))
				.andExpect(jsonPath("$.department.name").value("IT"));
	}

	@Test
	void createEmployee_returnsCreatedEmployee() throws Exception {
		Employee emp = createEmployee();

		when(service.createEmployee(any(Employee.class))).thenReturn(emp);

		mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content("""
				{
				    "name": "Swapnil",
				    "email": "swapnil@example.com",
				    "salary": 45000.00,
				    "department": {
				        "id": 1
				    }
				}
				""")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Swapnil"))
				.andExpect(jsonPath("$.email").value("swapnil@example.com"))
				.andExpect(jsonPath("$.salary").value(45000.00)).andExpect(jsonPath("$.department.id").value(1))
				.andExpect(jsonPath("$.department.name").value("IT"));
	}

	@Test
	void updateEmployee_returnsUpdatedEmployee() throws Exception {
		Employee emp = createEmployee("Swapnil Updated", "updated@example.com", "60000.00");

		when(service.updateEmployee(eq(1L), any(Employee.class))).thenReturn(emp);

		mockMvc.perform(put("/api/employees/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content("""
				{
				    "name": "Swapnil Updated",
				    "email": "updated@example.com",
				    "salary": 60000.00,
				    "department": {
				        "id": 1
				    }
				}
				""")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Swapnil Updated"))
				.andExpect(jsonPath("$.email").value("updated@example.com"))
				.andExpect(jsonPath("$.salary").value(60000.00)).andExpect(jsonPath("$.department.id").value(1))
				.andExpect(jsonPath("$.department.name").value("IT"));
	}

	@Test
	void deleteEmployee_returnsNoContent() throws Exception {
		mockMvc.perform(delete("/api/employees/{id}", 1L)).andExpect(status().isNoContent())
				.andExpect(content().string(""));

		verify(service).deleteEmployee(1L);
	}
}
