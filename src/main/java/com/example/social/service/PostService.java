package com.example.social.service;

import java.util.List;

import javax.transaction.Transactional;

import com.example.social.model.Post;
import com.example.social.repository.PostRepository;
import com.example.social.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Transactional
@Slf4j
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> listPosts() {
        return postRepository.findAll();
    }

    public Post addPost(Post post) {
        return postRepository.save(post);
    }

    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    public Post getPostByUserIdAndPostId(Long userId, Long postId) {
        return postRepository.findByUserIdAndId(userId, postId);
    }

    public Post updatePost(Long userId, Long postId, Post post) {

        var existingPost = postRepository.findByUserIdAndId(userId, postId);
        if (existingPost == null)
            return null;

        // update fields
        if (post.getContent() != null)
            existingPost.setContent(post.getContent());
        if (post.getLikes() != existingPost.getLikes())
            existingPost.setLikes(post.getLikes());

        return postRepository.save(existingPost);
    }

    public Post deletePost(Long userId, Long postId) {
    var existingPost = postRepository.findByUserIdAndId(userId, postId);
    if (existingPost == null) return null;

    postRepository.delete(existingPost);
    return existingPost;
    }
}
