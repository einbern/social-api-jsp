package com.example.social.repository;

import java.util.List;

import javax.transaction.Transactional;

import com.example.social.model.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource 
@Repository
@Transactional
public interface PostRepository extends JpaRepository<Post, Long> {

   public List<Post> findByUserId(Long userId); //keyword หาข้อมูลเรื่องนี้คือ jpa repository

   public Post findByUserIdAndId(Long userId, Long id); // able to declare findBy{Variable}

}
