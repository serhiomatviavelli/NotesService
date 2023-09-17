package ru.sberbank.jd.notesservice.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.sberbank.jd.notesservice.dao.entity.User;
import ru.sberbank.jd.notesservice.dao.repository.UserRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void getUserById() {
        User testUser = userService.getUserById("55d35870-bd93-11ed-afa1-0242ac120002");
        Mockito.verify(userRepository,Mockito.times(1)).findById(UUID.fromString("55d35870-bd93-11ed-afa1-0242ac120002"));
        assertNull(testUser);
    }

    @Test
    void addUser() {
        User user = new User();
        user.setName("Name");
        user.setPassword("psw");
        boolean isUser = userService.addUser(user);
        Mockito.verify(userRepository,Mockito.times(1)).findByName("Name");
        Mockito.verify(userRepository,Mockito.times(1)).save(user);
        assertFalse(isUser);
    }
}