package com.example.social.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DatabaseTable {
    private String tableQualifier;
    private String tableOwner;
    private String tableName;
    private String tableType;
    private String remarks;
}
