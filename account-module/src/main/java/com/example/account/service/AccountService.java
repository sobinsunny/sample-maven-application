package com.example.account.service;

import com.example.account.entity.Account;
import com.example.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account saveAccount(Account account) {
        validateAccount(account);
        if (account.getBalance() == null) {
            account.setBalance(0.0);
        }
        if (account.getActive() == null) {
            account.setActive(true);
        }
        if (accountRepository.findByAccountNumber(account.getAccountNumber()).isPresent()) {
            throw new IllegalArgumentException("Account number already exists");
        }
        if (accountRepository.findByUsername(account.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        return accountRepository.save(account);
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public Optional<Account> getAccountByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public List<Account> getAccountsByHolderName(String holderName) {
        return accountRepository.findByAccountHolderName(holderName);
    }

    public Account updateAccount(Long id, Account account) {
        Optional<Account> existingAccount = accountRepository.findById(id);
        if (existingAccount.isEmpty()) {
            throw new RuntimeException("Account not found with id: " + id);
        }

        Account accountToUpdate = existingAccount.get();
        if (account.getAccountHolderName() != null && !account.getAccountHolderName().trim().isEmpty()) {
            accountToUpdate.setAccountHolderName(account.getAccountHolderName());
        }
        if (account.getUsername() != null && !account.getUsername().trim().isEmpty()) {
            validateUsername(account.getUsername());
            accountToUpdate.setUsername(account.getUsername());
        }
        if (account.getPassword() != null && !account.getPassword().trim().isEmpty()) {
            validatePassword(account.getPassword());
            accountToUpdate.setPassword(account.getPassword());
        }
        if (account.getEmail() != null) {
            accountToUpdate.setEmail(account.getEmail());
        }
        if (account.getBalance() != null && account.getBalance() >= 0) {
            accountToUpdate.setBalance(account.getBalance());
        }
        if (account.getActive() != null) {
            accountToUpdate.setActive(account.getActive());
        }
        return accountRepository.save(accountToUpdate);
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    public boolean accountExists(Long id) {
        return accountRepository.existsById(id);
    }

    public boolean validateCredentials(String username, String password) {
        Optional<Account> account = accountRepository.findByUsername(username);
        return account.isPresent()
                && account.get().getActive()
                && account.get().getPassword().equals(password);
    }

    private void validateAccount(Account account) {
        if (account.getAccountNumber() == null || account.getAccountNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }
        if (account.getAccountHolderName() == null || account.getAccountHolderName().trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be empty");
        }
        validateUsername(account.getUsername());
        validatePassword(account.getPassword());
        if (account.getBalance() != null && account.getBalance() < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }
    }
}
