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

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.employee_management_system.entity.Department;
import com.example.employee_management_system.service.DepartmentService;

@WebMvcTest(DepartmentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DepartmentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DepartmentService service;

	private Department createDepartment() {
		return createDepartment("IT", "Some dummy desc");
	}

	private Department createDepartment(String name, String description) {
		Department dept = new Department();
		dept.setId(1L);
		dept.setName(name);
		dept.setDescription(description);
		return dept;
	}

	@Test
	void getAllDepartments_returnsDepartments() throws Exception {
		when(service.getAllDepartments()).thenReturn(List.of(createDepartment()));

		mockMvc.perform(get("/api/departments")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].name").value("IT"))
				.andExpect(jsonPath("$[0].description").value("Some dummy desc"));
	}

	@Test
	void getDepartmentById_returnsDepartment() throws Exception {
		when(service.getDepartmentById(1L)).thenReturn(createDepartment());

		mockMvc.perform(get("/api/departments/{id}", 1L)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("IT"))
				.andExpect(jsonPath("$.description").value("Some dummy desc"));
	}

	@Test
	void createDepartment_returnsCreatedDepartment() throws Exception {
		Department dept = createDepartment();

		when(service.createDepartment(any(Department.class))).thenReturn(dept);

		mockMvc.perform(post("/api/departments").contentType(MediaType.APPLICATION_JSON).content("""
				{
				    "name": "IT",
				    "description": "Some dummy desc"
				}
				""")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("IT"))
				.andExpect(jsonPath("$.description").value("Some dummy desc"));
	}

	@Test
	void updateDepartment_returnsUpdatedDepartment() throws Exception {
		Department dept = createDepartment("Engineering", "Engineering department");

		when(service.updateDepartment(eq(1L), any(Department.class))).thenReturn(dept);

		mockMvc.perform(put("/api/departments/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content("""
				{
				    "name": "Engineering",
				    "description": "Engineering department"
				}
				""")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Engineering"))
				.andExpect(jsonPath("$.description").value("Engineering department"));
	}

	@Test
	void deleteDepartment_returnsNoContent() throws Exception {
		mockMvc.perform(delete("/api/departments/{id}", 1L)).andExpect(status().isNoContent())
				.andExpect(content().string(""));

		verify(service).deleteDepartment(1L);
	}
}
