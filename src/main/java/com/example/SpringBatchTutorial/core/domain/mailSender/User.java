package com.example.SpringBatchTutorial.core.domain.mailSender;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class User {

    @Id
    private String email;

    private String name;
}
