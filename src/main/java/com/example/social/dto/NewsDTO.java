package com.example.social.dto;

import java.util.Date;

import lombok.Data;

@Data
public class NewsDTO {
    private String id;
    private String url;
    private String[] keywords;
    private String title;
    private String description;
    private String author;
    private Date date;
    private String sourceName;
    private String image;
}
