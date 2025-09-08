package com.payment.merchant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.common.util.BusinessIdGenerator;
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
@ExtendWith(MerchantServiceIntegrationTest.UserFriendlySummary.class)
public class MerchantServiceIntegrationTest {

    @TestConfiguration
    static class OverrideIdGeneratorConfig {
        @Bean
        @Primary
        public BusinessIdGenerator businessIdGenerator() {
            return new BusinessIdGenerator() {
                @Override
                public String generateMerchantId() {
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
    public void registerAndActivate_success() throws Exception {
        String code = ("ITMCH_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8)).toUpperCase();
        String payload = objectMapper.writeValueAsString(Map.of(
                "merchantName", "测试商户-IT",
                "merchantCode", code,
                "contactName", "测试",
                "contactPhone", "138" + String.format("%08d", System.currentTimeMillis() % 100000000),
                "contactEmail", code.toLowerCase() + "@example.com",
                "businessLicense", "911100000000" + (int)(Math.random()*10000),
                "legalPerson", "法人",
                "address", "测试地址"
        ));

        MvcResult reg = mockMvc.perform(post("/api/merchants/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        Map<?,?> regBody = objectMapper.readValue(reg.getResponse().getContentAsByteArray(), Map.class);
        Assertions.assertEquals(ErrorCodes.SUCCESS, regBody.get("code"));
        Map<?,?> data = (Map<?,?>) regBody.get("data");
        String merchantId = (String) data.get("merchantId");
        Assertions.assertNotNull(merchantId);

        MvcResult act = mockMvc.perform(post("/api/merchants/" + merchantId + "/activate"))
                .andExpect(status().isOk())
                .andReturn();
        Map<?,?> aBody = objectMapper.readValue(act.getResponse().getContentAsByteArray(), Map.class);
        Assertions.assertEquals(ErrorCodes.SUCCESS, aBody.get("code"));
    }

    @Test
    public void register_success() throws Exception {
        String code = ("ITMCH_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8)).toUpperCase();
        String payload = objectMapper.writeValueAsString(Map.of(
                "merchantName", "测试商户-IT",
                "merchantCode", code,
                "contactName", "测试",
                "contactPhone", "138" + String.format("%08d", System.currentTimeMillis() % 100000000),
                "contactEmail", code.toLowerCase() + "@example.com",
                "businessLicense", "911100000000" + String.format("%04d", (int)(Math.random()*10000)),
                "legalPerson", "法人",
                "address", "测试地址"
        ));
        mockMvc.perform(post("/api/merchants/register").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk());
    }

    @Test
    public void getById_success() throws Exception {
        String code = ("ITMCH_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8)).toUpperCase();
        String payload = objectMapper.writeValueAsString(Map.of(
                "merchantName", "测试商户",
                "merchantCode", code,
                "contactName", "测试",
                "contactPhone", "138" + String.format("%08d", System.currentTimeMillis() % 100000000),
                "contactEmail", code.toLowerCase() + "@example.com",
                "businessLicense", "911100000000" + String.format("%04d", (int)(Math.random()*10000)),
                "legalPerson", "法人",
                "address", "地址"
        ));
        MvcResult reg = mockMvc.perform(post("/api/merchants/register").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk()).andReturn();
        Map<?,?> regBody = objectMapper.readValue(reg.getResponse().getContentAsByteArray(), Map.class);
        String merchantId = (String)((Map<?,?>)regBody.get("data")).get("merchantId");
        mockMvc.perform(get("/api/merchants/" + merchantId)).andExpect(status().isOk());
    }

    @Test
    public void getByCode_success() throws Exception {
        String code = ("ITMCH_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8)).toUpperCase();
        String payload = objectMapper.writeValueAsString(Map.of(
                "merchantName", "测试商户",
                "merchantCode", code,
                "contactName", "测试",
                "contactPhone", "138" + String.format("%08d", System.currentTimeMillis() % 100000000),
                "contactEmail", code.toLowerCase() + "@example.com",
                "businessLicense", "911100000000" + String.format("%04d", (int)(Math.random()*10000)),
                "legalPerson", "法人",
                "address", "地址"
        ));
        mockMvc.perform(post("/api/merchants/register").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/merchants/code/" + code)).andExpect(status().isOk());
    }

    @Test
    public void update_success() throws Exception {
        String code = ("ITMCH_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8)).toUpperCase();
        String payload = objectMapper.writeValueAsString(Map.of(
                "merchantName", "测试商户",
                "merchantCode", code,
                "contactName", "测试",
                "contactPhone", "138" + String.format("%08d", System.currentTimeMillis() % 100000000),
                "contactEmail", code.toLowerCase() + "@example.com",
                "businessLicense", "911100000000" + String.format("%04d", (int)(Math.random()*10000)),
                "legalPerson", "法人",
                "address", "地址"
        ));
        MvcResult reg = mockMvc.perform(post("/api/merchants/register").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk()).andReturn();
        Map<?,?> regBody = objectMapper.readValue(reg.getResponse().getContentAsByteArray(), Map.class);
        String merchantId = (String)((Map<?,?>)regBody.get("data")).get("merchantId");
        String updatePayload = objectMapper.writeValueAsString(Map.of(
                "merchantName", "测试商户-更新",
                "contactName", "测试2",
                "contactPhone", "139" + String.format("%08d", System.currentTimeMillis() % 100000000),
                "contactEmail", (code.toLowerCase()+"+u@example.com"),
                "businessLicense", "911100000000" + String.format("%04d", (int)(Math.random()*10000)),
                "legalPerson", "法人2",
                "address", "地址-更新"
        ));
        mockMvc.perform(put("/api/merchants/" + merchantId).contentType(MediaType.APPLICATION_JSON).content(updatePayload))
                .andExpect(status().isOk());
    }

    @Test
    public void activate_success_single() throws Exception {
        String code = ("ITMCH_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8)).toUpperCase();
        String payload = objectMapper.writeValueAsString(Map.of(
                "merchantName", "测试商户",
                "merchantCode", code,
                "contactName", "测试",
                "contactPhone", "138" + String.format("%08d", System.currentTimeMillis() % 100000000),
                "contactEmail", code.toLowerCase() + "@example.com",
                "businessLicense", "911100000000" + String.format("%04d", (int)(Math.random()*10000)),
                "legalPerson", "法人",
                "address", "地址"
        ));
        MvcResult reg = mockMvc.perform(post("/api/merchants/register").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk()).andReturn();
        Map<?,?> regBody = objectMapper.readValue(reg.getResponse().getContentAsByteArray(), Map.class);
        String merchantId = (String)((Map<?,?>)regBody.get("data")).get("merchantId");
        mockMvc.perform(post("/api/merchants/" + merchantId + "/activate")).andExpect(status().isOk());
    }

    @Test
    public void deactivate_success_single() throws Exception {
        String code = ("ITMCH_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8)).toUpperCase();
        String payload = objectMapper.writeValueAsString(Map.of(
                "merchantName", "测试商户",
                "merchantCode", code,
                "contactName", "测试",
                "contactPhone", "138" + String.format("%08d", System.currentTimeMillis() % 100000000),
                "contactEmail", code.toLowerCase() + "@example.com",
                "businessLicense", "911100000000" + String.format("%04d", (int)(Math.random()*10000)),
                "legalPerson", "法人",
                "address", "地址"
        ));
        MvcResult reg = mockMvc.perform(post("/api/merchants/register").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk()).andReturn();
        Map<?,?> regBody = objectMapper.readValue(reg.getResponse().getContentAsByteArray(), Map.class);
        String merchantId = (String)((Map<?,?>)regBody.get("data")).get("merchantId");
        mockMvc.perform(post("/api/merchants/" + merchantId + "/deactivate")).andExpect(status().isOk());
    }

    @Test
    public void delete_success_single() throws Exception {
        String code = ("ITMCH_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8)).toUpperCase();
        String payload = objectMapper.writeValueAsString(Map.of(
                "merchantName", "测试商户",
                "merchantCode", code,
                "contactName", "测试",
                "contactPhone", "138" + String.format("%08d", System.currentTimeMillis() % 100000000),
                "contactEmail", code.toLowerCase() + "@example.com",
                "businessLicense", "911100000000" + String.format("%04d", (int)(Math.random()*10000)),
                "legalPerson", "法人",
                "address", "地址"
        ));
        MvcResult reg = mockMvc.perform(post("/api/merchants/register").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk()).andReturn();
        Map<?,?> regBody = objectMapper.readValue(reg.getResponse().getContentAsByteArray(), Map.class);
        String merchantId = (String)((Map<?,?>)regBody.get("data")).get("merchantId");
        mockMvc.perform(delete("/api/merchants/" + merchantId)).andExpect(status().isOk());
    }

    @Test
    public void register_duplicate_fail() throws Exception {
        String code = ("ITMCH_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8)).toUpperCase();
        String payload = objectMapper.writeValueAsString(Map.of(
                "merchantName", "测试商户",
                "merchantCode", code,
                "contactName", "测试",
                "contactPhone", "138" + String.format("%08d", System.currentTimeMillis() % 100000000),
                "contactEmail", code.toLowerCase() + "@example.com",
                "businessLicense", "911100000000" + String.format("%04d", (int)(Math.random()*10000)),
                "legalPerson", "法人",
                "address", "地址"
        ));
        mockMvc.perform(post("/api/merchants/register").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk());
        MvcResult dup = mockMvc.perform(post("/api/merchants/register").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk())
                .andReturn();
        Map<?,?> body = objectMapper.readValue(dup.getResponse().getContentAsByteArray(), Map.class);
        Assertions.assertNotEquals(ErrorCodes.SUCCESS, body.get("code"));
    }

    @Test
    public void getById_notFound_fail() throws Exception {
        MvcResult res = mockMvc.perform(get("/api/merchants/" + UUID.randomUUID().toString().replace("-", "")))
                .andExpect(status().isOk())
                .andReturn();
        Map<?,?> body = objectMapper.readValue(res.getResponse().getContentAsByteArray(), Map.class);
        Assertions.assertNotEquals(ErrorCodes.SUCCESS, body.get("code"));
    }

    @Test
    public void getByCode_notFound_fail() throws Exception {
        MvcResult res = mockMvc.perform(get("/api/merchants/code/NO_SUCH_CODE_" + UUID.randomUUID().toString().substring(0,6)))
                .andExpect(status().isOk())
                .andReturn();
        Map<?,?> body = objectMapper.readValue(res.getResponse().getContentAsByteArray(), Map.class);
        Assertions.assertNotEquals(ErrorCodes.SUCCESS, body.get("code"));
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
