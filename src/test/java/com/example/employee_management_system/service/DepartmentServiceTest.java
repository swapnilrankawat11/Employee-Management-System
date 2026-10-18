package com.example.employee_management_system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.employee_management_system.entity.Department;
import com.example.employee_management_system.exception.ResourceNotFoundException;
import com.example.employee_management_system.repository.DepartmentRepository;
import com.example.employee_management_system.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {

	@Mock
	private DepartmentRepository repo;

	@Mock
	private EmployeeRepository empRepo;

	@InjectMocks
	private DepartmentService service;

	@Test
	void getDepartmentById_whenDepartmentExists_returnsDepartment() {
		Department dept = new Department();
		when(repo.findById(1L)).thenReturn(Optional.of(dept));
		Department res = service.getDepartmentById(1L);
		assertSame(dept, res);
	}

	@Test
	void getDepartmentById_whenDepartmentDoesNotExists_throwsException() {
		when(repo.findById(1L)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> service.getDepartmentById(1L));
	}

	@Test
	void createDepartment_DepartmentNameIsUnique_saveAndReturnsDepartment() {
		Department dept = new Department();
		dept.setName("newdept");
		when(repo.existsByName("newdept")).thenReturn(false);
		when(repo.save(dept)).thenReturn(dept);

		Department res = service.createDepartment(dept);

		assertSame(dept, res);
		verify(repo).save(dept);

	}

	@Test
	void createDepartment_DepartmentNameIsNotUnique_throwsException() {
		Department dept = new Department();
		dept.setName("newdept");
		when(repo.existsByName("newdept")).thenReturn(true);

		assertThrows(IllegalStateException.class, () -> service.createDepartment(dept));
		verify(repo, never()).save(dept);
	}

	@Test
	void updateDepartment_AllConditionsSatisfied_updateAndReturnsDepartment() {
		Department existingDept = new Department();
		existingDept.setName("existingDept");
		existingDept.setDescription("existing dept description");

		when(repo.findById(1L)).thenReturn(Optional.of(existingDept));

		Department newDept = new Department();
		newDept.setName("newdept");
		newDept.setDescription("new dept description");

		when(repo.existsByName("newdept")).thenReturn(false);

		when(repo.save(existingDept)).thenReturn(existingDept);

		Department res = service.updateDepartment(1L, newDept);

		assertSame(existingDept, res);
		assertEquals("newdept", res.getName());
		assertEquals("new dept description", res.getDescription());

		verify(repo).save(existingDept);

	}

	@Test
	void updateDepartment_DepartmentDoesNotExists_throwsException() {
		Department dept = new Department();
		when(repo.findById(1L)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> service.updateDepartment(1L, dept));
		verify(repo, never()).save(dept);
	}

	@Test
	void updateDepartment_DepartmentNameIsNotUnique_throwsException() {
		Department existingDept = new Department();
		existingDept.setName("test");
		when(repo.findById(1L)).thenReturn(Optional.of(existingDept));
		Department newDept = new Department();
		newDept.setName("testnew");
		when(repo.existsByName("testnew")).thenReturn(true);
		assertThrows(IllegalStateException.class, () -> service.updateDepartment(1L, newDept));
		verify(repo, never()).save(existingDept);
	}

	@Test
	void deleteDepartment_AllConditionSatisfied_deleteDepartment() {
		Department dept = new Department();
		when(repo.findById(1L)).thenReturn(Optional.of(dept));
		when(empRepo.existsByDepartmentId(1L)).thenReturn(false);
		service.deleteDepartment(1L);
		verify(repo).deleteById(1L);
	}

	@Test
	void deleteDepartment_DepartmentDoesNotExists_throwsException() {
		when(repo.findById(1L)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> service.deleteDepartment(1L));
		verify(repo, never()).deleteById(1L);
	}

	@Test
	void deleteDeparment_DepartmentHasEmployees_throwsException() {
		Department dept = new Department();
		when(repo.findById(1L)).thenReturn(Optional.of(dept));
		when(empRepo.existsByDepartmentId(1L)).thenReturn(true);
		assertThrows(IllegalStateException.class, () -> service.deleteDepartment(1L));
		verify(repo, never()).deleteById(1L);
	}
}
