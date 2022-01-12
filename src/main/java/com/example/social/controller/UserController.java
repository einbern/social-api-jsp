package com.example.social.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Validator;

import com.example.social.dto.PostDTO;
import com.example.social.dto.UserDTO;
import com.example.social.model.Post;
import com.example.social.model.User;
import com.example.social.service.PostService;
import com.example.social.service.UserService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private Validator validator;

    @GetMapping
    public List<UserDTO> listUsers(
            @RequestParam(name = "minCredit", defaultValue = "") BigDecimal minCredit,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sortColumn) {
        log.info("listing user ...");

        var users = userService.listUsers(minCredit, page, size, sortColumn);
        var mapper = new ModelMapper();

        // return users.stream().map( (user) ->
        // { var UserDto = new UserDTO();
        // mapper.map(user, UserDto);
        // return UserDto;
        // }).collect(Collectors.toList());

        return users.stream().map(user -> mapper.map(user, UserDTO.class)).collect(Collectors.toList());
    }

    @GetMapping("{userId}")
    public ResponseEntity<Object> getUser(@PathVariable("userId") Long userId) {
        log.info("getting user, userId={}", userId);

        var user = userService.getUser(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        var mapper = new ModelMapper();
        return ResponseEntity.ok(mapper.map(user, UserDTO.class));
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody UserDTO newUser) {
        log.info("new user : ", newUser);

        try {
            var violations = validator.validate(newUser);
            if (!violations.isEmpty()) {
                var sb = new StringBuilder();
                for (var violation : violations) {
                    sb.append(violation.getPropertyPath() + " " + violation.getMessage());
                    sb.append("\n");
                }
                var errorMessage = sb.toString();
                return ResponseEntity.badRequest().body(errorMessage);
            }

            // find max ID
            // long maxId = users.stream().map(UserDTO::getId).reduce(Long::max).orElse(0L);
            // long maxId = 0L;

            // for(UserDTO user : users){
            // if (user.getId() > maxId) maxId = user.getId();
            // }
            // long newId = maxId + 1L;

            // newUser.setId(newId);
            // users.add(newUser);

            var mapper = new ModelMapper();

            var user = userService.addUser(mapper.map(newUser, User.class));
            return ResponseEntity.ok(mapper.map(user, UserDTO.class));
        } catch (Throwable t) {
            log.error("error occurs", t);
            return ResponseEntity.internalServerError().body(t.getMessage());
        }
    }

    @PutMapping("{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") Long userId, @RequestBody UserDTO newUser) {
        log.info("updating user: userId={}, newUser={}", userId, newUser);

        var loggedInUserId = (Long) RequestContextHolder.getRequestAttributes().getAttribute("userId",
                RequestAttributes.SCOPE_REQUEST);
        if (userId != loggedInUserId)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            var mapper = new ModelMapper();

            var user = userService.updateUser(userId, mapper.map(newUser, User.class));
            if (user == null)
                return ResponseEntity.notFound().build();

            return ResponseEntity.ok(mapper.map(user, UserDTO.class));
        } catch (Throwable t) {
            log.error("error occurs", t);
            return ResponseEntity.internalServerError().body(t.getMessage());
        }
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {

        var loggedInUserId = (Long) RequestContextHolder.getRequestAttributes().getAttribute("userId",
                RequestAttributes.SCOPE_REQUEST);
        if (userId != loggedInUserId)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            
        log.info("deleting user: userId={}", userId);

        try {
            var mapper = new ModelMapper();

            var user = userService.deleteUser(userId);
            if (user == null)
                return ResponseEntity.notFound().build();

            return ResponseEntity.ok(mapper.map(user, UserDTO.class));
        } catch (Throwable t) {
            log.error("error occurs", t);
            return ResponseEntity.internalServerError().body(t.getMessage());
        }
    }

    @PostMapping("{userId}/posts")
    public ResponseEntity<Object> addUserPost(@PathVariable("userId") Long userId, @RequestBody PostDTO newPost) {
        log.info("add post: userId={}, newPost={}", userId, newPost);
        newPost.setUserId(userId);
        log.info("test {}", newPost);

        try {
            var mapper = new ModelMapper();

            var post = postService.addPost(mapper.map(newPost, Post.class));

            return ResponseEntity.ok(mapper.map(post, PostDTO.class));
        } catch (Throwable t) {
            log.error("error occurs", t);
            return ResponseEntity.internalServerError().body(t.getMessage());
        }
    }

    @GetMapping("{userId}/posts")
    public ResponseEntity<Object> getPostsByUserId(@PathVariable("userId") Long userId) {
        log.info("getPostsByUserId, userId={}", userId);

        var posts = postService.getPostsByUserId(userId);
        if (posts == null) {
            return ResponseEntity.notFound().build();
        }

        var mapper = new ModelMapper();

        return ResponseEntity
                .ok(posts.stream().map(post -> mapper.map(post, PostDTO.class)).collect(Collectors.toList()));

    }

    @GetMapping("{userId}/posts/{postId}")
    public ResponseEntity<Object> getPostsByUserIdAndPostId(@PathVariable("userId") Long userId,
            @PathVariable("postId") Long postId) {
        log.info("getPostsByUserIdAndPostId, userId={}, postId={}", userId, postId);

        var post = postService.getPostByUserIdAndPostId(userId, postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        var mapper = new ModelMapper();
        return ResponseEntity.ok(mapper.map(post, PostDTO.class));

    }

    @PutMapping("{userId}/posts/{postId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") Long userId,
            @PathVariable("postId") Long postId, @RequestBody PostDTO newPost) {
        log.info("updating post: userId={}, postId={}, newPost={}", userId, postId, newPost);

        try {
            var mapper = new ModelMapper();

            var post = postService.updatePost(userId, postId, mapper.map(newPost, Post.class));
            if (post == null)
                return ResponseEntity.notFound().build();

            return ResponseEntity.ok(mapper.map(post, PostDTO.class));
        } catch (Throwable t) {
            log.error("error occurs", t);
            return ResponseEntity.internalServerError().body(t.getMessage());
        }
    }

    @DeleteMapping("{userId}/posts/{postId}")
    public ResponseEntity<Object> deletePost(@PathVariable Long userId, @PathVariable Long postId) {
        log.info("deleting user: userId={},  postId={}", userId, postId);

        try {
            var mapper = new ModelMapper();

            var post = postService.deletePost(userId, postId);
            if (post == null)
                return ResponseEntity.notFound().build();

            return ResponseEntity.ok(mapper.map(post, PostDTO.class));
        } catch (Throwable t) {
            log.error("error occurs", t);
            return ResponseEntity.internalServerError().body(t.getMessage());
        }
    }
}
