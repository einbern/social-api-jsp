package com.example.social.service;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import com.example.social.model.User;
import com.example.social.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Transactional
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // @Autowired
    // private EntityManager em;

    public List<User> listUsers(BigDecimal minCredit, int page, int size, String sortColumn) {

        Sort sort;
        if (sortColumn.startsWith("-")){
            sort = Sort.by(sortColumn.substring(1)).descending();
        }
        else{
            return userRepository.findAll();
        }

        if (minCredit != null) {


            return userRepository.findByCreditGreaterThanEqual(minCredit, PageRequest.of(page, size, sort))
                    .getContent();
        } else {
        log.info("test2");

            return userRepository.findAll(PageRequest.of(page, size, sort))
                    .getContent();
        }
        // return em.createQuery("select u from User u", User.class).getResultList();
        // return em.createQuery("from User", User.class).getResultList();
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User addUser(User user) {
        return userRepository.save(user);
        // em.persist(user);
        // return user;
    }

    public User updateUser(Long id, User user) {

        var existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null)
            return null;

        // update fields
        if (user.getPassword() != null)
            existingUser.setPassword(user.getPassword());
        if (user.getName() != null)
            existingUser.setName(user.getName());
        if (user.getEmail() != null)
            existingUser.setEmail(user.getEmail());

        return userRepository.save(existingUser);
    }

    public User deleteUser(Long id) {
        var existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null)
            return null;

        userRepository.delete(existingUser);
        return existingUser;
    }
}
