package com.example.social.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.social.dto.NewsDTO;
import com.example.social.model.DatabaseTable;
import com.example.social.model.News;
import com.example.social.model.User;

import org.apache.catalina.connector.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("demo")
@Slf4j
public class DemoController {

    @PersistenceContext
    EntityManager em;

    @GetMapping("users")
    public ResponseEntity<Object> listUserWithMinCredit(@RequestParam("minCredit") BigDecimal minCredit) {

        log.info("list user with minCredit={}", minCredit);

        // NamedNativeQuery
        // var result = em.createNamedQuery("enoughcredit", User.class)
        // .setParameter("mincredit", minCredit)
        // .getResultList();

        // NamedStoredProcedureQuery
        // var result =
        // em.createNamedStoredProcedureQuery("enoughcredit")
        // .setParameter("mincredit",minCredit)
        // .getResultList();

        var result = em.createNativeQuery("EXEC sp_enoughcredit @mincredit=:mincredit", User.class)
                .setParameter("mincredit", minCredit)
                .getResultList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("tables")
    public List<DatabaseTable> listTables() {

        @SuppressWarnings("unchecked")
        var result = (List<Object[]>) em.createNativeQuery("EXEC sp_tables @table_owner = :table_owner")
                .setParameter("table_owner", "dbo")
                .getResultList();

        return result
                .stream()
                .map(values -> new DatabaseTable(
                        (String) values[0],
                        (String) values[1],
                        (String) values[2],
                        (String) values[3],
                        (String) values[4]))
                .collect(Collectors.toList());
    }

    @Autowired
    RestTemplate restTemplate;
    
    @GetMapping("news")
    public List<NewsDTO> listNews() {

        var url = "https://opentest.win/flutter_training/news.php";

        var newsList = restTemplate.getForObject(url, News[].class);

        var mapper = new ModelMapper();

        return Arrays.asList(newsList)
        .stream()
        .map(o -> mapper.map(o, NewsDTO.class))
        .collect(Collectors.toList());
    }
}
