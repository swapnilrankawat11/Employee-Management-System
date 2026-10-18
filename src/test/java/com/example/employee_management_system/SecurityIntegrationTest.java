package com.example.employee_management_system;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final String USERNAME = "security_test_user";
	private static final String EMAIL = "security_test_user@example.com";
	private static final String PASSWORD = "Test@12345";

	private static final String ADMIN_USERNAME = "security_test_admin";
	private static final String ADMIN_PASSWORD = "Admin@12345";

	@BeforeEach
	void setUp() {
		appUserRepository.findByUsername(USERNAME).ifPresent(appUserRepository::delete);

		AppUser user = new AppUser();
		user.setUsername(USERNAME);
		user.setEmail(EMAIL);
		user.setPassword(passwordEncoder.encode(PASSWORD));
		user.setRole("USER");
		appUserRepository.saveAndFlush(user);

		appUserRepository.findByUsername(ADMIN_USERNAME).ifPresent(appUserRepository::delete);

		AppUser admin = new AppUser();
		admin.setUsername(ADMIN_USERNAME);
		admin.setEmail("security_test_admin@example.com");
		admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
		admin.setRole("ADMIN");
		appUserRepository.saveAndFlush(admin);
	}

	@Test
	void unauthenticatedGetRequestShouldReturn401() throws Exception {
		mockMvc.perform(get("/api/employees")).andExpect(status().isUnauthorized());
	}

	@Test
	void registrationEndpointShouldBePublicAndCreateUser() throws Exception {
		String requestBody = """
				{
				    "username": "new_security_user",
				    "email": "new_security_user@example.com",
				    "password": "NewUser@123"
				}
				""";

		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.message").value("Registration successful"))
				.andExpect(jsonPath("$.username").value("new_security_user"));
	}

	@Test
	void loginWithValidUserCredentialsShouldReturn200() throws Exception {
		mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("username", USERNAME)
				.param("password", PASSWORD)).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Login successful"));
	}

	@Test
	void loginWithInvalidPasswordShouldReturn401() throws Exception {
		mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("username", USERNAME)
				.param("password", "WrongPassword123")).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.error").value("Invalid username or password"));
	}

	@Test
	void userShouldAccessGetEmployeesButNotCreateEmployee() throws Exception {
		MvcResult loginResult = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", USERNAME).param("password", PASSWORD)).andExpect(status().isOk()).andReturn();

		MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

		mockMvc.perform(get("/api/employees").session(session)).andExpect(status().isOk());

		String employeeJson = """
				{
				    "name": "Security Test Employee",
				    "email": "security_employee@example.com",
				    "salary": 50000,
				    "department": {
				        "id": 1
				    }
				}
				""";

		mockMvc.perform(
				post("/api/employees").session(session).contentType(MediaType.APPLICATION_JSON).content(employeeJson))
				.andExpect(status().isForbidden());
	}

	@Test
	void adminShouldBeAllowedToWriteToApi() throws Exception {
		MvcResult loginResult = mockMvc
				.perform(post("/login").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("username", ADMIN_USERNAME).param("password", ADMIN_PASSWORD))
				.andExpect(status().isOk()).andReturn();

		MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

		String departmentJson = """
				{
				    "name": "Security Test Department",
				    "description": "Created by security integration test"
				}
				""";

		mockMvc.perform(post("/api/departments").session(session).contentType(MediaType.APPLICATION_JSON)
				.content(departmentJson)).andExpect(status().is2xxSuccessful());
	}

	@Test
	void logoutShouldInvalidateSession() throws Exception {
		MvcResult loginResult = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", USERNAME).param("password", PASSWORD)).andExpect(status().isOk()).andReturn();

		MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

		mockMvc.perform(get("/api/employees").session(session)).andExpect(status().isOk());

		mockMvc.perform(post("/logout").session(session)).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Logout successful"));

		mockMvc.perform(get("/api/employees").session(session)).andExpect(status().isUnauthorized());
	}
}
