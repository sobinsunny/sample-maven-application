package com.example.account.repository;

import com.example.account.entity.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    private Account testAccount;

    @BeforeEach
    public void setUp() {
        accountRepository.deleteAll();
        testAccount = new Account();
        testAccount.setAccountNumber("ACC-200");
        testAccount.setAccountHolderName("Bob Lee");
        testAccount.setUsername("bob");
        testAccount.setPassword("bob12345");
        testAccount.setEmail("bob@bank.com");
        testAccount.setBalance(250.0);
        testAccount.setActive(true);
    }

    @Test
    public void testSaveAccount() {
        Account saved = accountRepository.save(testAccount);

        assertNotNull(saved.getId());
        assertEquals("ACC-200", saved.getAccountNumber());
    }

    @Test
    public void testFindById() {
        Account saved = accountRepository.save(testAccount);
        Optional<Account> found = accountRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("bob", found.get().getUsername());
    }

    @Test
    public void testFindByAccountNumber() {
        accountRepository.save(testAccount);
        Optional<Account> found = accountRepository.findByAccountNumber("ACC-200");

        assertTrue(found.isPresent());
    }

    @Test
    public void testFindByUsername() {
        accountRepository.save(testAccount);
        Optional<Account> found = accountRepository.findByUsername("bob");

        assertTrue(found.isPresent());
    }

    @Test
    public void testFindByAccountHolderName() {
        accountRepository.save(testAccount);
        List<Account> accounts = accountRepository.findByAccountHolderName("Bob Lee");

        assertEquals(1, accounts.size());
    }

    @Test
    public void testFindByActive() {
        accountRepository.save(testAccount);
        List<Account> activeAccounts = accountRepository.findByActive(true);

        assertEquals(1, activeAccounts.size());
    }

    @Test
    public void testFindAll() {
        accountRepository.save(testAccount);
        assertEquals(1, accountRepository.findAll().size());
    }

    @Test
    public void testDeleteAccount() {
        Account saved = accountRepository.save(testAccount);
        accountRepository.deleteById(saved.getId());

        assertFalse(accountRepository.findById(saved.getId()).isPresent());
    }

    @Test
    public void testExistsById() {
        Account saved = accountRepository.save(testAccount);

        assertTrue(accountRepository.existsById(saved.getId()));
        assertFalse(accountRepository.existsById(999L));
    }
}
