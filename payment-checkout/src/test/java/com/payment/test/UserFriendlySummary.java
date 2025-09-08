package com.payment.test;

import org.junit.jupiter.api.extension.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserFriendlySummary implements TestWatcher, BeforeAllCallback, AfterAllCallback {
    private int succeeded = 0;
    private int failed = 0;
    private int aborted = 0;
    private int skipped = 0;
    private final List<String> succeededTests = new ArrayList<>();
    private final List<String> failedTests = new ArrayList<>();
    private final List<String> abortedTests = new ArrayList<>();
    private final List<String> skippedTests = new ArrayList<>();

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
        System.out.println("❌ FAILED: " + context.getDisplayName() + " -> " + (cause == null ? "" : cause.getMessage()));
        failedTests.add(context.getDisplayName() + " -> " + (cause == null ? "" : cause.getMessage()));
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        aborted++;
        System.out.println("⚠️  ABORTED: " + context.getDisplayName() + (cause == null ? "" : (" -> " + cause.getMessage())));
        abortedTests.add(context.getDisplayName() + (cause == null ? "" : (" -> " + cause.getMessage())));
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
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


