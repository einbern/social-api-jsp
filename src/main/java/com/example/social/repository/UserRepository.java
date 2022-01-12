package com.example.social.repository;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import com.example.social.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    public Page<User> findAll(Pageable pageable);

    public Page<User> findByCreditGreaterThanEqual(@Param("minCredit") BigDecimal minCredit, Pageable pageable);

    public User findByUsername(String username);
}
