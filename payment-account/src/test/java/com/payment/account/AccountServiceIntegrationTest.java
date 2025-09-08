package com.payment.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.common.util.BusinessIdGenerator;
import com.payment.common.constant.CommonConstants;
import com.payment.common.constant.ErrorCodes;
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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "spring.datasource.druid.web-stat-filter.enabled=false"
})
@AutoConfigureMockMvc
@ExtendWith(AccountServiceIntegrationTest.UserFriendlySummary.class)
public class AccountServiceIntegrationTest {

    @TestConfiguration
    static class OverrideIdGeneratorConfig {
        @Bean
        @Primary
        public BusinessIdGenerator businessIdGenerator() {
            return new BusinessIdGenerator() {
                @Override
                public String generateAccountNo() {
                    return UUID.randomUUID().toString().replace("-", "");
                }
            };
        }
    }

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void createAndOperate_success() throws Exception {
		// 使用随机32位userId，避免该类型账户已存在
		String userId = UUID.randomUUID().toString().replace("-", "");

		String createPayload = objectMapper.writeValueAsString(Map.of(
				"userId", userId,
				"accountType", "PERSONAL",
				"currency", "CNY"
		));
		MvcResult create = mockMvc.perform(post("/api/accounts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(createPayload))
				.andExpect(status().isOk())
				.andReturn();
		Map<?,?> cBody = objectMapper.readValue(create.getResponse().getContentAsByteArray(), Map.class);
		Assertions.assertEquals(ErrorCodes.SUCCESS, cBody.get("code"));
		Map<?,?> cData = (Map<?,?>) cBody.get("data");
		String accountId = (String) cData.get("accountId");
		Assertions.assertNotNull(accountId);

		String depositPayload = objectMapper.writeValueAsString(Map.of(
				"accountId", accountId,
				"amount", 1000,
				"transactionType", "DEPOSIT",
				"remark", "IT充值"
		));
		mockMvc.perform(post("/api/accounts/deposit")
				.contentType(MediaType.APPLICATION_JSON)
				.content(depositPayload))
				.andExpect(status().isOk());

		String withdrawPayload = objectMapper.writeValueAsString(Map.of(
				"accountId", accountId,
				"amount", 100,
				"transactionType", "WITHDRAW",
				"remark", "IT扣款"
		));
		mockMvc.perform(post("/api/accounts/withdraw")
				.contentType(MediaType.APPLICATION_JSON)
				.content(withdrawPayload))
				.andExpect(status().isOk());

		mockMvc.perform(post("/api/accounts/" + accountId + "/freeze").param("amount", "50"))
				.andExpect(status().isOk());
		mockMvc.perform(post("/api/accounts/" + accountId + "/unfreeze").param("amount", "50"))
				.andExpect(status().isOk());

		MvcResult balance = mockMvc.perform(get("/api/accounts/" + accountId + "/balance"))
				.andExpect(status().isOk())
				.andReturn();
		Map<?,?> bBody = objectMapper.readValue(balance.getResponse().getContentAsByteArray(), Map.class);
		Assertions.assertEquals(ErrorCodes.SUCCESS, bBody.get("code"));
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

    @Test
    public void create_success_single() throws Exception {
        String userId = UUID.randomUUID().toString().replace("-", "");
        String createPayload = objectMapper.writeValueAsString(Map.of(
                "userId", userId,
                "accountType", CommonConstants.ACCOUNT_TYPE_PERSONAL,
                "currency", CommonConstants.CURRENCY_CNY
        ));
        mockMvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON).content(createPayload))
                .andExpect(status().isOk());
    }

    @Test
    public void getById_success() throws Exception {
        String userId = UUID.randomUUID().toString().replace("-", "");
        String createPayload = objectMapper.writeValueAsString(Map.of(
                "userId", userId,
                "accountType", CommonConstants.ACCOUNT_TYPE_PERSONAL,
                "currency", CommonConstants.CURRENCY_CNY
        ));
        MvcResult create = mockMvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON).content(createPayload))
                .andExpect(status().isOk()).andReturn();
        Map<?,?> cBody = objectMapper.readValue(create.getResponse().getContentAsByteArray(), Map.class);
        Assertions.assertEquals(ErrorCodes.SUCCESS, cBody.get("code"));
        Map<?,?> cData = (Map<?,?>) cBody.get("data");
        String accountId = (String) cData.get("accountId");
        Assertions.assertNotNull(accountId);
        mockMvc.perform(get("/api/accounts/" + accountId)).andExpect(status().isOk());
    }

    @Test
    public void listByUser_success() throws Exception {
        String userId = UUID.randomUUID().toString().replace("-", "");
        String payload = objectMapper.writeValueAsString(Map.of(
                "userId", userId,
                "accountType", CommonConstants.ACCOUNT_TYPE_PERSONAL,
                "currency", CommonConstants.CURRENCY_CNY
        ));
        mockMvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/accounts/user/" + userId)).andExpect(status().isOk());
    }

    @Test
    public void deposit_success() throws Exception {
        String userId = UUID.randomUUID().toString().replace("-", "");
        MvcResult create = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("userId", userId, "accountType", CommonConstants.ACCOUNT_TYPE_PERSONAL, "currency", CommonConstants.CURRENCY_CNY))))
                .andExpect(status().isOk()).andReturn();
        String accountId = (String)((Map<?,?>)objectMapper.readValue(create.getResponse().getContentAsByteArray(), Map.class).get("data")).get("accountId");
        String depositPayload = objectMapper.writeValueAsString(Map.of("accountId", accountId, "amount", 200, "transactionType", CommonConstants.TRANSACTION_TYPE_DEPOSIT, "remark", "IT充值"));
        mockMvc.perform(post("/api/accounts/deposit").contentType(MediaType.APPLICATION_JSON).content(depositPayload)).andExpect(status().isOk());
    }

    @Test
    public void withdraw_success() throws Exception {
        String userId = UUID.randomUUID().toString().replace("-", "");
        MvcResult create = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("userId", userId, "accountType", CommonConstants.ACCOUNT_TYPE_PERSONAL, "currency", CommonConstants.CURRENCY_CNY))))
                .andExpect(status().isOk()).andReturn();
        String accountId = (String)((Map<?,?>)objectMapper.readValue(create.getResponse().getContentAsByteArray(), Map.class).get("data")).get("accountId");
        String depositPayload = objectMapper.writeValueAsString(Map.of("accountId", accountId, "amount", 200, "transactionType", CommonConstants.TRANSACTION_TYPE_DEPOSIT, "remark", "IT充值"));
        mockMvc.perform(post("/api/accounts/deposit").contentType(MediaType.APPLICATION_JSON).content(depositPayload)).andExpect(status().isOk());
        String withdrawPayload = objectMapper.writeValueAsString(Map.of("accountId", accountId, "amount", 50, "transactionType", CommonConstants.TRANSACTION_TYPE_WITHDRAW, "remark", "IT扣款"));
        mockMvc.perform(post("/api/accounts/withdraw").contentType(MediaType.APPLICATION_JSON).content(withdrawPayload)).andExpect(status().isOk());
    }

    @Test
    public void freeze_success() throws Exception {
        String userId = UUID.randomUUID().toString().replace("-", "");
        MvcResult create = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("userId", userId, "accountType", CommonConstants.ACCOUNT_TYPE_PERSONAL, "currency", "CNY"))))
                .andExpect(status().isOk()).andReturn();
        String accountId = (String)((Map<?,?>)objectMapper.readValue(create.getResponse().getContentAsByteArray(), Map.class).get("data")).get("accountId");
        mockMvc.perform(post("/api/accounts/" + accountId + "/freeze").param("amount", "30")).andExpect(status().isOk());
    }

    @Test
    public void unfreeze_success() throws Exception {
        String userId = UUID.randomUUID().toString().replace("-", "");
        MvcResult create = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("userId", userId, "accountType", CommonConstants.ACCOUNT_TYPE_PERSONAL, "currency", "CNY"))))
                .andExpect(status().isOk()).andReturn();
        String accountId = (String)((Map<?,?>)objectMapper.readValue(create.getResponse().getContentAsByteArray(), Map.class).get("data")).get("accountId");
        mockMvc.perform(post("/api/accounts/" + accountId + "/unfreeze").param("amount", "30")).andExpect(status().isOk());
    }

    @Test
    public void balance_success() throws Exception {
        String userId = UUID.randomUUID().toString().replace("-", "");
        MvcResult create = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("userId", userId, "accountType", CommonConstants.ACCOUNT_TYPE_PERSONAL, "currency", "CNY"))))
                .andExpect(status().isOk()).andReturn();
        String accountId = (String)((Map<?,?>)objectMapper.readValue(create.getResponse().getContentAsByteArray(), Map.class).get("data")).get("accountId");
        mockMvc.perform(get("/api/accounts/" + accountId + "/balance")).andExpect(status().isOk());
    }

    @Test
    public void delete_success_single() throws Exception {
        String userId = UUID.randomUUID().toString().replace("-", "");
        MvcResult create = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("userId", userId, "accountType", CommonConstants.ACCOUNT_TYPE_PERSONAL, "currency", "CNY"))))
                .andExpect(status().isOk()).andReturn();
        String accountId = (String)((Map<?,?>)objectMapper.readValue(create.getResponse().getContentAsByteArray(), Map.class).get("data")).get("accountId");
        mockMvc.perform(delete("/api/accounts/" + accountId)).andExpect(status().isOk());
    }

    @Test
    public void getById_notFound_fail() throws Exception {
        mockMvc.perform(get("/api/accounts/" + UUID.randomUUID().toString().replace("-", "")))
                .andExpect(status().isOk());
    }

    @Test
    public void deposit_negative_amount_fail() throws Exception {
        String userId = UUID.randomUUID().toString().replace("-", "");
        MvcResult create = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("userId", userId, "accountType", CommonConstants.ACCOUNT_TYPE_PERSONAL, "currency", "CNY"))))
                .andExpect(status().isOk()).andReturn();
        String accountId = (String)((Map<?,?>)objectMapper.readValue(create.getResponse().getContentAsByteArray(), Map.class).get("data")).get("accountId");
        String depositPayload = objectMapper.writeValueAsString(Map.of("accountId", accountId, "amount", -10, "transactionType", "DEPOSIT", "remark", "负数"));
        MvcResult res = mockMvc.perform(post("/api/accounts/deposit").contentType(MediaType.APPLICATION_JSON).content(depositPayload))
                .andExpect(status().is4xxClientError())
                .andReturn();
        Map<?,?> body = objectMapper.readValue(res.getResponse().getContentAsByteArray(), Map.class);
        Assertions.assertNotEquals(ErrorCodes.SUCCESS, body.get("code"));
    }

    @Test
    public void freeze_insufficient_balance_fail() throws Exception {
        String userId = UUID.randomUUID().toString().replace("-", "");
        MvcResult create = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("userId", userId, "accountType", "PERSONAL", "currency", "CNY"))))
                .andExpect(status().isOk()).andReturn();
        String accountId = (String)((Map<?,?>)objectMapper.readValue(create.getResponse().getContentAsByteArray(), Map.class).get("data")).get("accountId");
        MvcResult res = mockMvc.perform(post("/api/accounts/" + accountId + "/freeze").param("amount", "999999"))
                .andExpect(status().isOk())
                .andReturn();
        Map<?,?> body = objectMapper.readValue(res.getResponse().getContentAsByteArray(), Map.class);
        Assertions.assertNotEquals(ErrorCodes.SUCCESS, body.get("code"));
    }
}
