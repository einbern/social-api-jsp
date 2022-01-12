package com.example.social.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PostDTO {
    private Long id;
    private Long userId;
    private String content;
    private int likes;
}
