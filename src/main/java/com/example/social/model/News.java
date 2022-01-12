package com.example.social.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class News {
     
    private String id;
    private String url;
    private String[] keywords;
    private String title;
    private String description;
    private String author;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "en_US")
    private Date date;
    @JsonProperty("source_name")
    private String sourceName;
    private String image;
}
