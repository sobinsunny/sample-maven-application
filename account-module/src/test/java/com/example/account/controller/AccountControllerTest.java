package com.example.account.controller;

import com.example.account.entity.Account;
import com.example.account.repository.AccountRepository;
import com.example.account.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.example.account.AccountModuleTestApplication.class)
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Account testAccount;

    @BeforeEach
    public void setUp() {
        accountRepository.deleteAll();
        testAccount = new Account();
        testAccount.setAccountNumber("ACC-100");
        testAccount.setAccountHolderName("Alice Brown");
        testAccount.setUsername("alice");
        testAccount.setPassword("alice1234");
        testAccount.setEmail("alice@bank.com");
        testAccount.setBalance(500.0);
        testAccount.setActive(true);
    }

    @Test
    public void testCreateAccount() throws Exception {
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAccount)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value("ACC-100"))
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    public void testCreateAccountInvalidData() throws Exception {
        testAccount.setUsername("");
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAccount)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAccountById() throws Exception {
        Account saved = accountService.saveAccount(testAccount);

        mockMvc.perform(get("/api/accounts/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    public void testGetAccountByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/accounts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllAccounts() throws Exception {
        accountService.saveAccount(testAccount);

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("ACC-100"));
    }

    @Test
    public void testGetAccountByNumber() throws Exception {
        accountService.saveAccount(testAccount);

        mockMvc.perform(get("/api/accounts/number/ACC-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    public void testGetAccountByUsername() throws Exception {
        accountService.saveAccount(testAccount);

        mockMvc.perform(get("/api/accounts/username/alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountHolderName").value("Alice Brown"));
    }

    @Test
    public void testGetAccountsByHolderName() throws Exception {
        accountService.saveAccount(testAccount);

        mockMvc.perform(get("/api/accounts/holder/Alice Brown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("alice"));
    }

    @Test
    public void testValidateCredentials() throws Exception {
        accountService.saveAccount(testAccount);

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "alice");
        credentials.put("password", "alice1234");

        mockMvc.perform(post("/api/accounts/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    public void testValidateCredentialsInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/accounts/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateAccount() throws Exception {
        Account saved = accountService.saveAccount(testAccount);

        Account update = new Account();
        update.setAccountHolderName("Alice Updated");
        update.setBalance(750.0);

        mockMvc.perform(put("/api/accounts/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountHolderName").value("Alice Updated"));
    }

    @Test
    public void testUpdateAccountNotFound() throws Exception {
        Account update = new Account();
        update.setAccountHolderName("Missing");

        mockMvc.perform(put("/api/accounts/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteAccount() throws Exception {
        Account saved = accountService.saveAccount(testAccount);

        mockMvc.perform(delete("/api/accounts/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteAccountNotFound() throws Exception {
        mockMvc.perform(delete("/api/accounts/999"))
                .andExpect(status().isNotFound());
    }
}
