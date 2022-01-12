package com.example.social;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.example.social.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.util.LinkedMultiValueMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControlloerTests {
    
    @Autowired
    private TestRestTemplate testRestTemplate;

    private UserDTO user1, user2, user3;

    private String authorization;

    @Test @Order(1)
    public void testListUser() {

        log.info("test list user...");
        var users = testRestTemplate.getForObject("/users", UserDTO[].class);

        assertEquals(0, users.length, "user list is not empty");
    }

    @Test @Order(2)
    public void testAddUser1() throws Exception {
        
        var newUser = new UserDTO();
        newUser.setUsername("test1");
        newUser.setPassword("password");
        newUser.setName("Test1 Surname");
        newUser.setEmail("test1@gmail.com");
        newUser.setCredit(BigDecimal.ZERO);
        newUser.setRegisterDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-01-01 12:00:00"));

        // var createdUser = this.testRestTemplate.postForObject("/users", newUser, UserDTO.class);

        var reponseEntity = testRestTemplate.postForEntity("/users", newUser, String.class);
        assertEquals(HttpStatus.OK, reponseEntity.getStatusCode(), "status is not ok");
        
        var objectMapper = new ObjectMapper();
        var createdUser = objectMapper.readValue(reponseEntity.getBody(), UserDTO.class);

        // expected new ID
        newUser.setId(1L);
        assertEquals(newUser, createdUser, "created user is not the same");
        
        user1 = newUser;
    }
    
    @Test @Order(3)
    public void testAddUser2() throws Exception {
        
        var newUser = new UserDTO();
        newUser.setUsername("test2");
        newUser.setPassword("password");
        newUser.setName("Test2 Surname");
        newUser.setEmail("test2@gmail.com");
        newUser.setCredit(BigDecimal.ZERO);
        newUser.setRegisterDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-02-01 12:00:00"));

        var reponseEntity = testRestTemplate.postForEntity("/users", newUser, String.class);
        assertEquals(HttpStatus.OK, reponseEntity.getStatusCode(), "status is not ok");
        
        var objectMapper = new ObjectMapper();
        var createdUser = objectMapper.readValue(reponseEntity.getBody(), UserDTO.class);

        // expected new ID
        newUser.setId(2L);
        assertEquals(newUser, createdUser, "created user is not the same");

        user2 = newUser; 
    }

    @Test @Order(4)
    public void testAddUser3() throws Exception {
        
        var newUser = new UserDTO();
        newUser.setUsername("test3");
        newUser.setPassword("password");
        newUser.setName("Test3 Surname");
        newUser.setEmail("test3@gmail.com");
        newUser.setCredit(BigDecimal.ZERO);
        newUser.setRegisterDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-02-01 12:00:00"));

        var responseEntity = testRestTemplate.postForEntity("/users", newUser, String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "status is not ok");
        
        var objectMapper = new ObjectMapper();
        var createdUser = objectMapper.readValue(responseEntity.getBody(), UserDTO.class);

        // expected new ID
        newUser.setId(3L);
        assertEquals(newUser, createdUser, "created user is not the same");

        user3 = newUser;
    }

    @Test @Order(5)
    public void testAddUser4WithBlankEmail() throws Exception {
        
        var newUser = new UserDTO();
        newUser.setUsername("test4");
        newUser.setPassword("password");
        newUser.setName("Test4 Surname");
        newUser.setEmail("");
        newUser.setCredit(BigDecimal.ZERO);
        newUser.setRegisterDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-02-01 12:00:00"));

        var reponseEntity = testRestTemplate.postForEntity("/users", newUser, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, reponseEntity.getStatusCode(), "status must be BAD_REQUEST");
        
        var errorMessage = reponseEntity.getBody();
        assertTrue(errorMessage.contains("email"), "error message is not correct");
    }

    @Test @Order(6)
    public void testList3Users() throws Exception {

        log.info("test list user...");
        var users = testRestTemplate.getForObject("/users?sort=id", UserDTO[].class);
      
        var expectedUsers = new UserDTO[] { user1, user2, user3};
        
        assertEquals(expectedUsers.length, users.length, "user list size not correct");
        for(int i=0; i < expectedUsers.length; i++) {
            var expectedUser = expectedUsers[i];
            var user = users[i];

            assertUserEquals(expectedUser, user);
        }

    }

    private void assertUserEquals(UserDTO expectedUser, UserDTO user) {
        assertEquals(expectedUser.getId(), user.getId(), "id does not match");
        assertEquals(expectedUser.getUsername(), user.getUsername(), "username does not match");
        assertEquals(expectedUser.getPassword(), user.getPassword(), "password does not match");
        assertEquals(expectedUser.getName(), user.getName(), "name does not match");
        assertEquals(expectedUser.getEmail(), user.getEmail(), "email does not match");
        assertTrue(expectedUser.getCredit().compareTo(user.getCredit()) == 0, "credit does not match");
        assertEquals(expectedUser.getRegisterDate(), user.getRegisterDate(), "registerDate does not match");
    }

    @Test @Order(7)
    public void testGetUser2() {

        var user = testRestTemplate.getForObject("/users/2", UserDTO.class);
        assertUserEquals(user2, user);
    }

    @Test @Order(8)
    public void testLogin() {

        var map = new LinkedMultiValueMap<String, String>();
        map.add("username", "test1");
        map.add("password", "password");

        var requestEntity = RequestEntity.post("/login")
        .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .body(map);

        var responseEntity = testRestTemplate.exchange(requestEntity, String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "status is not ok");

        var apiKey = responseEntity.getBody();
        assertNotNull(apiKey, "api key is null");
        assertTrue(apiKey.length() > 0, "api key is blank");

        authorization = "Bearer " + apiKey;
    }

    @Test @Order(9)
    public void testUpdateUser1() {

        var newUser = new UserDTO();
        newUser.setName("Test1 EDITED");
        newUser.setEmail("test1@gmail.com");

        var requestEntity = RequestEntity.put("/users/1")
        .header("Authorization", authorization)
        .body(newUser); 

        var responseEntity = testRestTemplate.exchange(requestEntity, UserDTO.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "status is not ok");
        
        var updateUser = responseEntity.getBody();

        // expected new
        user1.setName(newUser.getName());
        user1.setEmail(newUser.getEmail());

        assertUserEquals(user1, updateUser);
    }

    @Test @Order(10)
    public void testList3UsersAfterUpdateUser1() throws Exception {

        log.info("test list user...");
        var users = testRestTemplate.getForObject("/users?sort=id", UserDTO[].class);
      
        var expectedUsers = new UserDTO[] { user1, user2, user3};
        
        assertEquals(expectedUsers.length, users.length, "user list size not correct");
        for(int i=0; i < expectedUsers.length; i++) {
            var expectedUser = expectedUsers[i];
            var user = users[i];

            assertUserEquals(expectedUser, user);
        }

    }

    @Test @Order(11)
    public void testDeleteUser2() {

        var requestEntity = RequestEntity.delete("/users/2")
        .header("Authorization", authorization).build(); 

        var responseEntity = testRestTemplate.exchange(requestEntity, UserDTO.class);
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode(), "status is not UNAUTHORIZED");
    }

    @Test @Order(12)
    public void testList3UsersAfterDeleteUser2() {

        log.info("test list user...");
        var users = testRestTemplate.getForObject("/users?sort=id", UserDTO[].class);
      
        var expectedUsers = new UserDTO[] { user1, user2, user3};
        
        assertEquals(expectedUsers.length, users.length, "user list size not correct");
        for(int i=0; i < expectedUsers.length; i++) {
            var expectedUser = expectedUsers[i];
            var user = users[i];

            assertUserEquals(expectedUser, user);
        }
    }

    @Test @Order(13)
    public void testDeleteUser1() {

        var requestEntity = RequestEntity.delete("/users/1")
        .header("Authorization", authorization).build(); 

        var responseEntity = testRestTemplate.exchange(requestEntity, UserDTO.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "status is not OK");

        var deleteUser = responseEntity.getBody();

        assertUserEquals(user1, deleteUser);
        
    }

    @Test @Order(14)
    public void testGetUserAfterDeleteUser1() {

        var responseEntity = testRestTemplate.getForEntity("/users/1", UserDTO.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode(), "status is not NOT_FOUND");

    }
}

