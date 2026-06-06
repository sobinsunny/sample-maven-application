package com.example.repository;

import com.example.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        testUser = new User();
        testUser.setName("John Doe");
        testUser.setAge(30);
        testUser.setEmail("john@example.com");
        testUser.setPhone("1234567890");
    }

    @Test
    public void testSaveUser() {
        User savedUser = userRepository.save(testUser);

        assertNotNull(savedUser.getId());
        assertEquals("John Doe", savedUser.getName());
        assertEquals(30, savedUser.getAge());
    }

    @Test
    public void testFindUserById() {
        User savedUser = userRepository.save(testUser);
        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());

        assertTrue(retrievedUser.isPresent());
        assertEquals("John Doe", retrievedUser.get().getName());
    }

    @Test
    public void testFindUserByIdNotFound() {
        Optional<User> retrievedUser = userRepository.findById(999L);
        assertFalse(retrievedUser.isPresent());
    }

    @Test
    public void testFindByName() {
        userRepository.save(testUser);
        Optional<User> retrievedUser = userRepository.findByName("John Doe");

        assertTrue(retrievedUser.isPresent());
        assertEquals("John Doe", retrievedUser.get().getName());
    }

    @Test
    public void testFindByNameNotFound() {
        Optional<User> retrievedUser = userRepository.findByName("NonExistent");
        assertFalse(retrievedUser.isPresent());
    }

    @Test
    public void testFindByAge() {
        userRepository.save(testUser);

        User testUser2 = new User();
        testUser2.setName("Jane Doe");
        testUser2.setAge(30);
        userRepository.save(testUser2);

        List<User> users = userRepository.findByAge(30);
        assertEquals(2, users.size());
    }

    @Test
    public void testFindByAgeNoResults() {
        List<User> users = userRepository.findByAge(99);
        assertEquals(0, users.size());
    }

    @Test
    public void testFindByEmail() {
        userRepository.save(testUser);
        List<User> users = userRepository.findByEmail("john@example.com");

        assertEquals(1, users.size());
        assertEquals("john@example.com", users.get(0).getEmail());
    }

    @Test
    public void testFindByEmailNotFound() {
        List<User> users = userRepository.findByEmail("nonexistent@example.com");
        assertEquals(0, users.size());
    }

    @Test
    public void testFindAll() {
        userRepository.save(testUser);

        User testUser2 = new User();
        testUser2.setName("Jane Doe");
        testUser2.setAge(25);
        userRepository.save(testUser2);

        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());
    }

    @Test
    public void testFindAllEmpty() {
        List<User> users = userRepository.findAll();
        assertEquals(0, users.size());
    }

    @Test
    public void testUpdateUser() {
        User savedUser = userRepository.save(testUser);

        savedUser.setName("Jane Doe");
        savedUser.setAge(25);
        User updatedUser = userRepository.save(savedUser);

        assertEquals("Jane Doe", updatedUser.getName());
        assertEquals(25, updatedUser.getAge());
    }

    @Test
    public void testDeleteUser() {
        User savedUser = userRepository.save(testUser);

        userRepository.delete(savedUser);

        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
        assertFalse(retrievedUser.isPresent());
    }

    @Test
    public void testDeleteById() {
        User savedUser = userRepository.save(testUser);

        userRepository.deleteById(savedUser.getId());

        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
        assertFalse(retrievedUser.isPresent());
    }

    @Test
    public void testExistsById() {
        User savedUser = userRepository.save(testUser);

        assertTrue(userRepository.existsById(savedUser.getId()));
        assertFalse(userRepository.existsById(999L));
    }

    @Test
    public void testCount() {
        userRepository.save(testUser);

        User testUser2 = new User();
        testUser2.setName("Jane Doe");
        testUser2.setAge(25);
        userRepository.save(testUser2);

        assertEquals(2, userRepository.count());
    }
}
