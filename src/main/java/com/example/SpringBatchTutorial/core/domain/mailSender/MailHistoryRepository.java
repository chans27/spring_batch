package com.example.SpringBatchTutorial.core.domain.mailSender;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MailHistoryRepository extends JpaRepository<MailHistory, Integer> {
}
