package com.pms.crosscutting;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LifecycleEventConsumerTest {

    @Test
    void contextLoads() {
        // Test context loading
    }
}