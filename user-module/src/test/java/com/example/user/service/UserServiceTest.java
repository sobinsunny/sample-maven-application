package com.example.user.service;

import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.example.user.UserModuleTestApplication.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

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
        User savedUser = userService.saveUser(testUser);

        assertNotNull(savedUser.getId());
        assertEquals("John Doe", savedUser.getName());
        assertEquals(30, savedUser.getAge());
    }

    @Test
    public void testSaveUserWithInvalidName() {
        testUser.setName("");
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(testUser));
    }

    @Test
    public void testSaveUserWithNullName() {
        testUser.setName(null);
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(testUser));
    }

    @Test
    public void testSaveUserWithInvalidAge() {
        testUser.setAge(-5);
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(testUser));
    }

    @Test
    public void testSaveUserWithAgeOver150() {
        testUser.setAge(151);
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(testUser));
    }

    @Test
    public void testSaveUserWithNullAge() {
        testUser.setAge(null);
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(testUser));
    }

    @Test
    public void testGetUserById() {
        User savedUser = userService.saveUser(testUser);
        Optional<User> retrievedUser = userService.getUserById(savedUser.getId());

        assertTrue(retrievedUser.isPresent());
        assertEquals("John Doe", retrievedUser.get().getName());
    }

    @Test
    public void testGetUserByIdNotFound() {
        Optional<User> retrievedUser = userService.getUserById(999L);
        assertFalse(retrievedUser.isPresent());
    }

    @Test
    public void testGetAllUsers() {
        userService.saveUser(testUser);

        User testUser2 = new User();
        testUser2.setName("Jane Doe");
        testUser2.setAge(25);
        userService.saveUser(testUser2);

        List<User> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    public void testGetAllUsersEmpty() {
        List<User> users = userService.getAllUsers();
        assertEquals(0, users.size());
    }

    @Test
    public void testGetUserByName() {
        userService.saveUser(testUser);
        Optional<User> retrievedUser = userService.getUserByName("John Doe");

        assertTrue(retrievedUser.isPresent());
        assertEquals("John Doe", retrievedUser.get().getName());
    }

    @Test
    public void testGetUserByNameNotFound() {
        Optional<User> retrievedUser = userService.getUserByName("NonExistent");
        assertFalse(retrievedUser.isPresent());
    }

    @Test
    public void testGetUsersByAge() {
        userService.saveUser(testUser);

        User testUser2 = new User();
        testUser2.setName("Jane Doe");
        testUser2.setAge(30);
        userService.saveUser(testUser2);

        List<User> users = userService.getUsersByAge(30);
        assertEquals(2, users.size());
    }

    @Test
    public void testGetUsersByAgeNoResults() {
        List<User> users = userService.getUsersByAge(99);
        assertEquals(0, users.size());
    }

    @Test
    public void testUpdateUser() {
        User savedUser = userService.saveUser(testUser);

        User updatedUser = new User();
        updatedUser.setName("Jane Doe");
        updatedUser.setAge(25);

        User result = userService.updateUser(savedUser.getId(), updatedUser);

        assertEquals("Jane Doe", result.getName());
        assertEquals(25, result.getAge());
    }

    @Test
    public void testUpdateUserPartial() {
        User savedUser = userService.saveUser(testUser);

        User updatedUser = new User();
        updatedUser.setName("Jane Doe");

        User result = userService.updateUser(savedUser.getId(), updatedUser);

        assertEquals("Jane Doe", result.getName());
        assertEquals(30, result.getAge());
    }

    @Test
    public void testUpdateUserNotFound() {
        User updatedUser = new User();
        updatedUser.setName("Jane Doe");

        assertThrows(RuntimeException.class, () -> userService.updateUser(999L, updatedUser));
    }

    @Test
    public void testUpdateUserWithInvalidAge() {
        User savedUser = userService.saveUser(testUser);

        User updatedUser = new User();
        updatedUser.setAge(-5);

        User result = userService.updateUser(savedUser.getId(), updatedUser);

        assertEquals(30, result.getAge());
    }

    @Test
    public void testDeleteUser() {
        User savedUser = userService.saveUser(testUser);

        userService.deleteUser(savedUser.getId());

        Optional<User> retrievedUser = userService.getUserById(savedUser.getId());
        assertFalse(retrievedUser.isPresent());
    }

    @Test
    public void testUserExists() {
        User savedUser = userService.saveUser(testUser);

        assertTrue(userService.userExists(savedUser.getId()));
        assertFalse(userService.userExists(999L));
    }
}
