package com.gym.crm.workload.logging;

import com.gym.crm.workload.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {TransactionLoggingFilter.class, TransactionLoggingFilterTest.TestController.class})
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class TransactionLoggingFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGenerateNewTransactionId_WhenNoHeaderPresent() throws Exception {
        var actual = mockMvc.perform(get("/test"))
                .andExpect(status().isOk())
                .andReturn();

        String txId = actual.getResponse().getHeader("X-Transaction-Id");
        assertThat(txId).isNotBlank();
    }

    @Test
    void shouldReuseTransactionId_WhenHeaderPresent() throws Exception {
        String expected = "12345-test-id";

        mockMvc.perform(get("/test")
                        .header("X-Transaction-Id", expected))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Transaction-Id", expected));
    }

    @RestController
    static class TestController {
        @GetMapping("/test")
        public String testMessage() {
            return "OK";
        }
    }
}