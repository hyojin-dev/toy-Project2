package com.example.banktoyproject.elasticsearch.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Subway {
    @CsvBindByName
    private String code;
    @CsvBindByName
    private String station;
    @CsvBindByName
    private String line;
    @CsvBindByName
    private String excode;
//
//    private String chosung;
//    private String jamo;
//    private String engtokor;
}
