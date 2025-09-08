package com.payment.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.common.util.BusinessIdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
		"server.servlet.context-path=/payment-user",
		"spring.main.allow-bean-definition-overriding=true"
})
@AutoConfigureMockMvc
@ExtendWith(UserServiceIntegrationTest.UserFriendlySummary.class)
public class UserServiceIntegrationTest {

	@TestConfiguration
	static class OverrideIdGeneratorConfig {
		@Bean
		@Primary
		public BusinessIdGenerator businessIdGenerator() {
			return new BusinessIdGenerator() {
				@Override
				public String generateUserId() {
					// Return exactly 32 characters to fit VARCHAR(32)
					return UUID.randomUUID().toString().replace("-", "");
				}
			};
		}
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private Map<String, String> registerUser() throws Exception {
		String username = "it_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
		// 生成符合手机号格式的号码：138 + 8位随机数字，确保总长度为11位
		// 使用时间戳确保唯一性，并保证11位格式
		long timestamp = System.currentTimeMillis();
		String phone = "138" + String.format("%08d", timestamp % 100000000);
		
		String email = username + "@example.com";
		String payload = objectMapper.writeValueAsString(Map.of(
				"username", username,
				"password", "123456",
				"confirmPassword", "123456",
				"email", email,
				"phone", phone
		));
		MvcResult reg = mockMvc.perform(post("/api/user/register").contentType(MediaType.APPLICATION_JSON).content(payload))
				.andExpect(status().isOk()).andReturn();
		Map<?,?> regBody = objectMapper.readValue(reg.getResponse().getContentAsByteArray(), Map.class);
		Map<?,?> data = (Map<?,?>) regBody.get("data");
		if (data == null) {
			throw new RuntimeException("注册失败，返回数据为空: " + regBody);
		}
		return Map.of(
				"userId", (String) data.get("userId"),
				"username", username,
				"phone", phone,
				"email", email
		);
	}

	@Test
	public void register_success_single() throws Exception {
		registerUser();
	}

	@Test
	public void getByUsername_success() throws Exception {
		Map<String,String> u = registerUser();
		mockMvc.perform(get("/api/user/username/" + u.get("username")))
				.andExpect(status().isOk());
	}

	@Test
	public void getByPhone_success() throws Exception {
		Map<String,String> u = registerUser();
		mockMvc.perform(get("/api/user/phone/" + u.get("phone")))
				.andExpect(status().isOk());
	}

	@Test
	public void getByEmail_success() throws Exception {
		Map<String,String> u = registerUser();
		mockMvc.perform(get("/api/user/email/" + u.get("email")))
				.andExpect(status().isOk());
	}

	@Test
	public void checkUsername_success() throws Exception {
		Map<String,String> u = registerUser();
		mockMvc.perform(get("/api/user/check/username/" + u.get("username")))
				.andExpect(status().isOk());
	}

	@Test
	public void checkPhone_success() throws Exception {
		Map<String,String> u = registerUser();
		mockMvc.perform(get("/api/user/check/phone/" + u.get("phone")))
				.andExpect(status().isOk());
	}

	@Test
	public void checkEmail_success() throws Exception {
		Map<String,String> u = registerUser();
		mockMvc.perform(get("/api/user/check/email/" + u.get("email")))
				.andExpect(status().isOk());
	}

	@Test
	public void page_success() throws Exception {
		registerUser();
		mockMvc.perform(get("/api/user/page").param("pageNum", "1").param("pageSize", "5"))
				.andExpect(status().isOk());
	}

	@Test
	public void getById_success() throws Exception {
		Map<String,String> u = registerUser();
		mockMvc.perform(get("/api/user/" + u.get("userId")))
				.andExpect(status().isOk());
	}

	@Test
	public void update_success() throws Exception {
		Map<String,String> u = registerUser();
		String updPayload = objectMapper.writeValueAsString(Map.of(
				"username", u.get("username"),
				"password", "123456",
				"confirmPassword", "123456",
				"email", u.get("email"),
				"phone", u.get("phone")
		));
		mockMvc.perform(put("/api/user/" + u.get("userId")).contentType(MediaType.APPLICATION_JSON).content(updPayload))
				.andExpect(status().isOk());
	}

	@Test
	public void changePassword_success() throws Exception {
		Map<String,String> u = registerUser();
		mockMvc.perform(put("/api/user/" + u.get("userId") + "/password").param("oldPassword", "123456").param("newPassword", "654321"))
				.andExpect(status().isOk());
	}

	@Test
	public void freeze_success_single() throws Exception {
		Map<String,String> u = registerUser();
		mockMvc.perform(put("/api/user/" + u.get("userId") + "/freeze"))
				.andExpect(status().isOk());
	}

	@Test
	public void unfreeze_success_single() throws Exception {
		Map<String,String> u = registerUser();
		mockMvc.perform(put("/api/user/" + u.get("userId") + "/unfreeze"))
				.andExpect(status().isOk());
	}

	@Test
	public void delete_success_single() throws Exception {
		Map<String,String> u = registerUser();
		mockMvc.perform(delete("/api/user/" + u.get("userId")))
				.andExpect(status().isOk());
	}

	@Test
	public void register_duplicate_fail() throws Exception {
		// first register
		String username = "it_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
		// 生成符合手机号格式的号码：138 + 8位随机数字，确保总长度为11位
		long timestamp = System.currentTimeMillis();
		String phone = "138" + String.format("%08d", timestamp % 100000000);
		String email = username + "@example.com";
		String payload = objectMapper.writeValueAsString(Map.of(
				"username", username,
				"password", "123456",
				"confirmPassword", "123456",
				"email", email,
				"phone", phone
		));
		mockMvc.perform(post("/api/user/register").contentType(MediaType.APPLICATION_JSON).content(payload))
				.andExpect(status().isOk());
		// duplicate
		MvcResult dup = mockMvc.perform(post("/api/user/register").contentType(MediaType.APPLICATION_JSON).content(payload))
				.andExpect(status().is5xxServerError())
				.andReturn();
		Map<?,?> body = objectMapper.readValue(dup.getResponse().getContentAsByteArray(), Map.class);
		Object code = body.get("code");
		Assertions.assertNotEquals("0000", code);
	}

	@Test
	public void changePassword_wrongOld_fail() throws Exception {
		Map<String,String> u = registerUser();
		MvcResult res = mockMvc.perform(put("/api/user/" + u.get("userId") + "/password").param("oldPassword", "wrong").param("newPassword", "654321"))
				.andExpect(status().is5xxServerError())
				.andReturn();
		Map<?,?> body = objectMapper.readValue(res.getResponse().getContentAsByteArray(), Map.class);
		Assertions.assertNotEquals("0000", body.get("code"));
	}

	@Test
	public void getByUsername_notFound_fail() throws Exception {
		String random = "u_" + UUID.randomUUID().toString().replace("-", "");
		MvcResult res = mockMvc.perform(get("/api/user/username/" + random))
				.andExpect(status().is5xxServerError())
				.andReturn();
		Map<?,?> body = objectMapper.readValue(res.getResponse().getContentAsByteArray(), Map.class);
		Assertions.assertNotEquals("0000", body.get("code"));
	}


    static class UserFriendlySummary implements TestWatcher, BeforeAllCallback, AfterAllCallback {
        private int succeeded = 0;
        private int failed = 0;
        private int aborted = 0;
        private int skipped = 0;
        private final java.util.List<String> succeededTests = new java.util.ArrayList<>();
        private final java.util.List<String> failedTests = new java.util.ArrayList<>();
        private final java.util.List<String> abortedTests = new java.util.ArrayList<>();
        private final java.util.List<String> skippedTests = new java.util.ArrayList<>();

        @Override
        public void beforeAll(ExtensionContext context) {
            System.out.println("\n===== 🧪 Running " + context.getDisplayName() + " =====");
        }

        @Override
        public void testSuccessful(ExtensionContext context) {
            succeeded++;
            System.out.println("✅ PASSED: " + context.getDisplayName());
            succeededTests.add(context.getDisplayName());
        }

        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            failed++;
            System.out.println("❌ FAILED: " + context.getDisplayName() + " -> " + cause.getMessage());
            failedTests.add(context.getDisplayName() + " -> " + (cause == null ? "" : cause.getMessage()));
        }

        @Override
        public void testAborted(ExtensionContext context, Throwable cause) {
            aborted++;
            System.out.println("⚠️  ABORTED: " + context.getDisplayName());
            abortedTests.add(context.getDisplayName() + (cause == null ? "" : (" -> " + cause.getMessage())));
        }

        @Override
        public void testDisabled(ExtensionContext context, java.util.Optional<String> reason) {
            skipped++;
            System.out.println("⏭  SKIPPED: " + context.getDisplayName() + reason.map(r -> " (" + r + ")").orElse(""));
            skippedTests.add(context.getDisplayName() + reason.map(r -> " (" + r + ")").orElse(""));
        }

        @Override
        public void afterAll(ExtensionContext context) {
            int total = succeeded + failed + aborted + skipped;
            System.out.println("----- 📊 Summary for " + context.getDisplayName() + " -----");
            System.out.println("Total: " + total + ", ✅ Passed: " + succeeded + ", ❌ Failed: " + failed + ", ⚠️ Aborted: " + aborted + ", ⏭ Skipped: " + skipped);
            if (!succeededTests.isEmpty()) {
                System.out.println("-- ✅ Passed details:");
                succeededTests.forEach(name -> System.out.println("   - " + name));
            }
            if (!failedTests.isEmpty()) {
                System.out.println("-- ❌ Failed details:");
                failedTests.forEach(name -> System.out.println("   - " + name));
            }
            if (!abortedTests.isEmpty()) {
                System.out.println("-- ⚠️  Aborted details:");
                abortedTests.forEach(name -> System.out.println("   - " + name));
            }
            if (!skippedTests.isEmpty()) {
                System.out.println("-- ⏭ Skipped details:");
                skippedTests.forEach(name -> System.out.println("   - " + name));
            }
            System.out.println("===============================================\n");
        }
    }
}
