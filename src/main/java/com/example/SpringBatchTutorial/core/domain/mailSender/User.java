package com.example.SpringBatchTutorial.core.domain.mailSender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String email;

    private String name;

    @CreatedBy
    private LocalDateTime last_login;

    private int status;

    public void updateStatus() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime lastLoginTime = getLast_login();

//        if (lastLoginTime != null && ChronoUnit.DAYS.between(lastLoginTime, currentTime) >= 30) {
        if (lastLoginTime != null && ChronoUnit.MINUTES.between(lastLoginTime, currentTime) >= 3) {
            setStatus(1); //비활성화
        } else {
            setStatus(0);
        }
    }

}
