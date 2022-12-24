package com.example.banktoyproject.elasticsearch.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SubwayDocument {
    private String code;
    private String station;
    private String chosung;
    private String jamo;
    private String engtokor;
    private String line;
    private String excode;
}
