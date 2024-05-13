package com.example.domain.service;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Service {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Long id;

    private String serviceName;

    private String tag;     // ex) event

    public Service(String serviceName, String tag){
        this.serviceName = serviceName;
        this.tag = tag;
    }
}
