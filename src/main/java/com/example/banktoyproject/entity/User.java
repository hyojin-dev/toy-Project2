package com.example.banktoyproject.entity;

import com.example.banktoyproject.config.Timestamped.Timestamped;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String username;

    @JsonIgnore // user 조회했을 때 json에서 안 뜨게 함
    @Column(nullable = false)
    private String password;

    private String email;
}