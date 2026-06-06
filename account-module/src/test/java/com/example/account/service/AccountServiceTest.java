package com.example.account.service;

import com.example.account.entity.Account;
import com.example.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.example.account.AccountModuleTestApplication.class)
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    private Account testAccount;

    @BeforeEach
    public void setUp() {
        accountRepository.deleteAll();
        testAccount = new Account();
        testAccount.setAccountNumber("ACC-001");
        testAccount.setAccountHolderName("John Smith");
        testAccount.setUsername("jsmith");
        testAccount.setPassword("secret123");
        testAccount.setEmail("john@bank.com");
        testAccount.setBalance(1000.0);
        testAccount.setActive(true);
    }

    @Test
    public void testSaveAccount() {
        Account saved = accountService.saveAccount(testAccount);

        assertNotNull(saved.getId());
        assertEquals("ACC-001", saved.getAccountNumber());
        assertEquals("jsmith", saved.getUsername());
    }

    @Test
    public void testSaveAccountWithDefaultBalance() {
        testAccount.setBalance(null);
        Account saved = accountService.saveAccount(testAccount);

        assertEquals(0.0, saved.getBalance());
        assertTrue(saved.getActive());
    }

    @Test
    public void testSaveAccountInvalidAccountNumber() {
        testAccount.setAccountNumber("");
        assertThrows(IllegalArgumentException.class, () -> accountService.saveAccount(testAccount));
    }

    @Test
    public void testSaveAccountInvalidUsername() {
        testAccount.setUsername("");
        assertThrows(IllegalArgumentException.class, () -> accountService.saveAccount(testAccount));
    }

    @Test
    public void testSaveAccountInvalidPassword() {
        testAccount.setPassword("abc");
        assertThrows(IllegalArgumentException.class, () -> accountService.saveAccount(testAccount));
    }

    @Test
    public void testSaveAccountNegativeBalance() {
        testAccount.setBalance(-1.0);
        assertThrows(IllegalArgumentException.class, () -> accountService.saveAccount(testAccount));
    }

    @Test
    public void testSaveAccountDuplicateNumber() {
        accountService.saveAccount(testAccount);

        Account duplicate = new Account();
        duplicate.setAccountNumber("ACC-001");
        duplicate.setAccountHolderName("Jane");
        duplicate.setUsername("jane");
        duplicate.setPassword("pass1234");

        assertThrows(IllegalArgumentException.class, () -> accountService.saveAccount(duplicate));
    }

    @Test
    public void testGetAccountById() {
        Account saved = accountService.saveAccount(testAccount);
        Optional<Account> found = accountService.getAccountById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("ACC-001", found.get().getAccountNumber());
    }

    @Test
    public void testGetAccountByIdNotFound() {
        assertFalse(accountService.getAccountById(999L).isPresent());
    }

    @Test
    public void testGetAllAccounts() {
        accountService.saveAccount(testAccount);
        assertEquals(1, accountService.getAllAccounts().size());
    }

    @Test
    public void testGetAccountByNumber() {
        accountService.saveAccount(testAccount);
        Optional<Account> found = accountService.getAccountByNumber("ACC-001");

        assertTrue(found.isPresent());
    }

    @Test
    public void testGetAccountByUsername() {
        accountService.saveAccount(testAccount);
        Optional<Account> found = accountService.getAccountByUsername("jsmith");

        assertTrue(found.isPresent());
    }

    @Test
    public void testGetAccountsByHolderName() {
        accountService.saveAccount(testAccount);
        List<Account> accounts = accountService.getAccountsByHolderName("John Smith");

        assertEquals(1, accounts.size());
    }

    @Test
    public void testUpdateAccount() {
        Account saved = accountService.saveAccount(testAccount);

        Account update = new Account();
        update.setAccountHolderName("John Updated");
        update.setBalance(2000.0);

        Account result = accountService.updateAccount(saved.getId(), update);

        assertEquals("John Updated", result.getAccountHolderName());
        assertEquals(2000.0, result.getBalance());
    }

    @Test
    public void testUpdateAccountCredentials() {
        Account saved = accountService.saveAccount(testAccount);

        Account update = new Account();
        update.setUsername("newuser");
        update.setPassword("newpass123");

        Account result = accountService.updateAccount(saved.getId(), update);

        assertEquals("newuser", result.getUsername());
        assertEquals("newpass123", result.getPassword());
    }

    @Test
    public void testUpdateAccountNotFound() {
        Account update = new Account();
        update.setAccountHolderName("Missing");

        assertThrows(RuntimeException.class, () -> accountService.updateAccount(999L, update));
    }

    @Test
    public void testDeleteAccount() {
        Account saved = accountService.saveAccount(testAccount);
        accountService.deleteAccount(saved.getId());

        assertFalse(accountService.getAccountById(saved.getId()).isPresent());
    }

    @Test
    public void testAccountExists() {
        Account saved = accountService.saveAccount(testAccount);

        assertTrue(accountService.accountExists(saved.getId()));
        assertFalse(accountService.accountExists(999L));
    }

    @Test
    public void testValidateCredentialsSuccess() {
        accountService.saveAccount(testAccount);

        assertTrue(accountService.validateCredentials("jsmith", "secret123"));
    }

    @Test
    public void testValidateCredentialsWrongPassword() {
        accountService.saveAccount(testAccount);

        assertFalse(accountService.validateCredentials("jsmith", "wrong"));
    }

    @Test
    public void testValidateCredentialsInactiveAccount() {
        testAccount.setActive(false);
        accountService.saveAccount(testAccount);

        assertFalse(accountService.validateCredentials("jsmith", "secret123"));
    }
}
