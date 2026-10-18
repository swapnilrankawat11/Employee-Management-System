package com.example.employee_management_system.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.example.employee_management_system.repository.DepartmentRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class DepartmentIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private DepartmentRepository repo;

	@BeforeEach
	void setUp() {
		repo.deleteAll();
	}

	@Test
	void createDepartment_savesDepartmentToDatabase() throws Exception {
		mockMvc.perform(post("/api/departments").contentType(MediaType.APPLICATION_JSON).content("""
				{
				    "name": "IT",
				    "description": "Some dummy desc"
				}
				""")).andExpect(status().isOk()).andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.name").value("IT"))
				.andExpect(jsonPath("$.description").value("Some dummy desc"));

		Department savedDept = repo.findByName("IT").orElseThrow(() -> new AssertionError("Department was not saved!"));

		assertTrue(savedDept.getId() > 0);
		assertEquals("IT", savedDept.getName());
		assertEquals("Some dummy desc", savedDept.getDescription());
	}

	private Department createDepartment(String name, String desc) {
		Department dept = new Department();
		dept.setName(name);
		dept.setDescription(desc);
		return dept;
	}

	@Test
	void getAllDepartments_returnsDepartmentsFromDatabase() throws Exception {
		Department dept = createDepartment("IT", "Some dummy desc");
		Department savedDept = repo.saveAndFlush(dept);

		mockMvc.perform(get("/api/departments")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(savedDept.getId().intValue()))
				.andExpect(jsonPath("$[0].name").value("IT"))
				.andExpect(jsonPath("$[0].description").value("Some dummy desc"));
	}

	@Test
	void getDepartmentById_returnsDepartmentFromDatabase() throws Exception {
		Department dept = createDepartment("IT", "Some dummy desc");
		Department savedDept = repo.saveAndFlush(dept);

		mockMvc.perform(get("/api/departments/{id}", savedDept.getId())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(savedDept.getId().intValue()))
				.andExpect(jsonPath("$.name").value("IT"))
				.andExpect(jsonPath("$.description").value("Some dummy desc"));
	}

	@Test
	void updateDepartment_returnsUpdatedDepartmentFromDatabase() throws Exception {
		Department dept = createDepartment("IT", "Some dummy desc");
		Department savedDept = repo.saveAndFlush(dept);

		mockMvc.perform(
				put("/api/departments/{id}", savedDept.getId()).contentType(MediaType.APPLICATION_JSON).content("""
						{
						    "name": "Engineering",
						    "description": "Engineering department"
						}
						""")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(savedDept.getId()))
				.andExpect(jsonPath("$.name").value("Engineering"))
				.andExpect(jsonPath("$.description").value("Engineering department"));

		Department updatedDept = repo.findById(savedDept.getId())
				.orElseThrow(() -> new AssertionError("Updated department not found"));

		assertTrue(updatedDept.getId() > 0);
		assertEquals("Engineering", updatedDept.getName());
		assertEquals("Engineering department", updatedDept.getDescription());
	}

	@Test
	void deleteDepartment_returnsNothing() throws Exception {
		Department dept = createDepartment("IT", "Some dummy desc");
		Department savedDept = repo.saveAndFlush(dept);

		Long savedDeptId = savedDept.getId();

		mockMvc.perform(delete("/api/departments/{id}", savedDeptId)).andExpect(status().isNoContent());

		Optional<Department> deletedDept = repo.findById(savedDeptId);

		assertFalse(deletedDept.isPresent());

	}
}
