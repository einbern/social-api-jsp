package com.example.social.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Entity
@Data
@Table(name = "users", indexes = {
        @Index(columnList = "username"),
        @Index(columnList = "name"),
        @Index(columnList = "credit"),
        @Index(columnList = "email"),
        @Index(columnList = "registerDate"),
})
@EntityListeners(AuditingEntityListener.class)

@NamedNativeQuery(
    name = "enoughcredit", 
    query = "EXEC sp_enoughcredit @mincredit=:mincredit", 
    resultClass = User.class)

@NamedStoredProcedureQuery(
    name = "enoughcredit", 
    procedureName = "sp_enoughcredit", 
    resultClasses = { User.class },
    parameters = {
        @StoredProcedureParameter(name = "mincredit", type = BigDecimal.class, mode = ParameterMode.IN)
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @Min(0)
    private BigDecimal credit;

    @NotNull
    @Past
    private Date registerDate;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private Date createdDate;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private Date lastModifiedDate;
}
