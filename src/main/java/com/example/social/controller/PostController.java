package com.example.social.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.example.social.dto.PostDTO;
import com.example.social.service.PostService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/posts")
@Slf4j
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping
    public List<PostDTO> listPosts() {
        log.info("listing post ...");

        var posts = postService.listPosts();
        var mapper = new ModelMapper();

        return posts.stream().map(post -> mapper.map(post, PostDTO.class)).collect(Collectors.toList());
    }
}
