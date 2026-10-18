
package com.example.employee_management_system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.employee_management_system.entity.Department;
import com.example.employee_management_system.entity.Employee;
import com.example.employee_management_system.exception.ResourceNotFoundException;
import com.example.employee_management_system.repository.DepartmentRepository;
import com.example.employee_management_system.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

	@Mock
	private EmployeeRepository repo;

	@Mock
	private DepartmentRepository departmentRepo;

	@InjectMocks
	private EmployeeService service;

	@Test
	void getEmployeeById_whenEmployeeExists_returnsEmployee() {
		Employee emp = new Employee();

		when(repo.findById(1L)).thenReturn(Optional.of(emp));

		Employee res = service.getEmployeeById(1L);

		assertSame(emp, res);
	}

	@Test
	void getEmployeeById_whenEmployeeDoesNotExist_throwsException() {
		when(repo.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.getEmployeeById(99L));
	}

	@Test
	void createEmployee_whenEmailIsUnique_savesAndReturnsEmployee() {
		Department department = createDepartment();

		Employee emp = createEmployee(department);
		emp.setEmail("test@gmail.com");

		when(repo.existsByEmail("test@gmail.com")).thenReturn(false);
		when(departmentRepo.findById(1L)).thenReturn(Optional.of(department));
		when(repo.save(emp)).thenReturn(emp);

		Employee res = service.createEmployee(emp);

		assertSame(emp, res);
		assertSame(department, res.getDepartment());

		verify(departmentRepo).findById(1L);
		verify(repo).save(emp);
	}

	@Test
	void createEmployee_whenEmailAlreadyExists_throwsException() {
		Employee employee = new Employee();
		employee.setEmail("existing@example.com");

		when(repo.existsByEmail("existing@example.com")).thenReturn(true);

		assertThrows(IllegalStateException.class, () -> service.createEmployee(employee));

		verify(repo, never()).save(employee);
		verify(departmentRepo, never()).findById(1L);
	}

	@Test
	void updateEmployee_whenAllConditionsSatisfied_updatesAndReturnsEmployee() {
		Department department = createDepartment();

		Employee existingEmp = createEmployee(department);
		existingEmp.setName("Old Name");
		existingEmp.setEmail("existing@gmail.com");
		existingEmp.setSalary(BigDecimal.valueOf(30000));

		when(repo.findById(1L)).thenReturn(Optional.of(existingEmp));

		Employee newEmp = createEmployee(department);
		newEmp.setName("New Name");
		newEmp.setEmail("newmail@gmail.com");
		newEmp.setSalary(BigDecimal.valueOf(40000));

		when(repo.existsByEmail("newmail@gmail.com")).thenReturn(false);
		when(departmentRepo.findById(1L)).thenReturn(Optional.of(department));
		when(repo.save(existingEmp)).thenReturn(existingEmp);

		Employee res = service.updateEmployee(1L, newEmp);

		assertSame(existingEmp, res);
		assertEquals("New Name", res.getName());
		assertEquals("newmail@gmail.com", res.getEmail());
		assertEquals(0, new BigDecimal("40000.00").compareTo(res.getSalary()));
		assertSame(department, res.getDepartment());

		verify(departmentRepo).findById(1L);
		verify(repo).save(existingEmp);
	}

	@Test
	void updateEmployee_whenEmployeeDoesNotExist_throwsException() {
		Employee emp = new Employee();

		when(repo.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.updateEmployee(1L, emp));

		verify(repo, never()).save(existingEmpPlaceholder());
	}

	@Test
	void updateEmployee_whenEmailIsNotUnique_throwsException() {
		Employee existingEmp = new Employee();
		existingEmp.setEmail("existing@example.com");

		when(repo.findById(1L)).thenReturn(Optional.of(existingEmp));

		Employee newEmp = new Employee();
		newEmp.setEmail("existing123@example.com");

		when(repo.existsByEmail("existing123@example.com")).thenReturn(true);

		assertThrows(IllegalStateException.class, () -> service.updateEmployee(1L, newEmp));

		verify(repo, never()).save(existingEmp);
		verify(departmentRepo, never()).findById(1L);
	}

	@Test
	void updateEmployee_whenEmailRemainsUnchanged_updatesAndReturnsEmployee() {
		Department department = createDepartment();

		Employee existingEmp = createEmployee(department);
		existingEmp.setEmail("existing@gmail.com");

		when(repo.findById(1L)).thenReturn(Optional.of(existingEmp));

		Employee newEmp = createEmployee(department);
		newEmp.setEmail("existing@gmail.com");

		when(departmentRepo.findById(1L)).thenReturn(Optional.of(department));
		when(repo.save(existingEmp)).thenReturn(existingEmp);

		Employee res = service.updateEmployee(1L, newEmp);

		assertEquals(newEmp.getEmail(), res.getEmail());
		verify(departmentRepo).findById(1L);
		verify(repo).save(existingEmp);
	}

	@Test
	void deleteEmployee_whenEmployeeExists_deletesEmployee() {
		Employee emp = new Employee();

		when(repo.findById(1L)).thenReturn(Optional.of(emp));

		service.deleteEmployee(1L);

		verify(repo).deleteById(1L);
	}

	@Test
	void deleteEmployee_whenEmployeeDoesNotExist_throwsException() {
		when(repo.findById(1L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> service.deleteEmployee(1L));

		verify(repo, never()).deleteById(1L);
	}

	private Department createDepartment() {
		Department department = new Department();
		department.setId(1L);
		department.setName("IT");
		department.setDescription("Test department");
		return department;
	}

	private Employee createEmployee(Department department) {
		Employee employee = new Employee();
		employee.setName("Swapnil");
		employee.setEmail("swapnil@example.com");
		employee.setSalary(new BigDecimal("45000.00"));
		employee.setDepartment(department);
		return employee;
	}

	private Employee existingEmpPlaceholder() {
		return new Employee();
	}
}
