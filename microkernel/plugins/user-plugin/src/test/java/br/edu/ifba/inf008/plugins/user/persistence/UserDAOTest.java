package br.edu.ifba.inf008.plugins.user.persistence;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.edu.ifba.inf008.shell.model.User;
 
public class UserDAOTest {

    private UserDAO userDAO;
    private User testUser;
    private List<User> createdUsers;

    private User saveAndTrack(User user){
        userDAO.save(user);
        createdUsers.add(user);
        return user;
    }

    @BeforeEach
    void setUp(){
        userDAO = new UserDAO();
        createdUsers = new ArrayList<>();

        String uniqueEmail = "test_" + System.currentTimeMillis() + "@test.com";
        testUser = new User("Test User", uniqueEmail);
    }

    @AfterEach
    void cleanup(){
        for (User user : createdUsers) {
            try{
                if(user.getUserId() != null) {
                    userDAO.delete(user);
                }
            } catch (Exception e) {
                System.err.println("Error deleting user: " + user.getEmail() + " - " + e.getMessage());
            }
        }
        createdUsers.clear();
    }

    @Test
    void testSaveUser() {
        User createdUser = saveAndTrack(testUser);
        assertNotNull(createdUser.getUserId(), "User ID should not be null");
    }

    @Test
    void testFindUserById(){
        User createdUser = saveAndTrack(testUser);

        User foundUser = userDAO.findById(createdUser.getUserId());

        assertNotNull(foundUser, "Found user should not be null");
        assertEquals("Test User", foundUser.getName(), "User name should match");
    }

    @Test
    void testFindUserByEmail(){
        User createdUser = saveAndTrack(testUser);

        User foundUser = userDAO.findByEmail(createdUser.getEmail());

        assertNotNull(foundUser, "Found user should not be null");
        assertEquals(testUser.getEmail(), foundUser.getEmail(), "User email should match");
    }

    @Test
    void testFindAllUsers(){
        User user1 = saveAndTrack(new User("Test User 1", "test1_" + System.currentTimeMillis() + "@test.com"));
        User user2 = saveAndTrack(new User("Test User 2", "test2_" + System.currentTimeMillis() + "@test.com"));
        User user3 = saveAndTrack(new User("Test User 3", "test3_" + System.currentTimeMillis() + "@test.com"));

        List<User> users = userDAO.findAll();

        assertNotNull(users, "User list should not be null");
        assertTrue(users.size() >= 3, "There should be at least 3 users in the list");  
        assertTrue(users.contains(user1), "User list should contain user1");
        assertTrue(users.contains(user2), "User list should contain user2");
        assertTrue(users.contains(user3), "User list should contain user3");
    }

    @Test
    void testUpdateUser(){
        User createdUser = saveAndTrack(testUser);
        String updatedName = "Updated User Name";
        createdUser.setName(updatedName);

        userDAO.update(createdUser);

        User updatedUser = userDAO.findById(createdUser.getUserId());
        assertNotNull(updatedUser, "Updated user should not be null");
        assertEquals(updatedName, updatedUser.getName(), "User name should be updated");
    }

    @Test
    void testDeleteUser(){
        User createdUser = saveAndTrack(testUser);
        userDAO.delete(createdUser);
        User deletedUser = userDAO.findById(createdUser.getUserId());
        assertNull(deletedUser, "Deleted user should be null");
    }
}