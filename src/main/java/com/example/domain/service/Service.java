package com.example.domain.service;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Service {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Long id;

    private String serviceName;

    private String tag;     // ex) event

    private String url;

    public Service(String serviceName, String tag, String url){
        this.serviceName = serviceName;
        this.tag = tag;
        this.url = url;
    }
}
