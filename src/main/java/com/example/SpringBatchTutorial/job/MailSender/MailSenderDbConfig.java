package com.example.SpringBatchTutorial.job.MailSender;

import com.example.SpringBatchTutorial.core.domain.mailSender.MailHistory;
import com.example.SpringBatchTutorial.core.domain.mailSender.MailHistoryRepository;
import com.example.SpringBatchTutorial.core.domain.mailSender.User;
import com.example.SpringBatchTutorial.core.domain.mailSender.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MailSenderDbConfig {

    private final JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailHistoryRepository mailHistoryRepository;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;



    @Bean
    public Job mailSenderDbJob(Step mailSenderDbStep) {
        return jobBuilderFactory.get("mailSenderDbJob")
                .incrementer(new RunIdIncrementer())
                .start(mailSenderDbStep)
                .build();
    }

    @JobScope
    @Bean
    public Step mailSenderDbStep(
            ItemReader mailSenderReader,
            ItemProcessor mailSenderProcessor,
            ItemWriter mailSenderWriter) {

        return stepBuilderFactory.get("mailSenderDbStep")
                .chunk(5)
                .reader(mailSenderReader)
                .processor(mailSenderProcessor)
                .writer(mailSenderWriter)
                .build();
    }

    @StepScope
    @Bean
    public RepositoryItemReader<User> mailSenderReader() {
        return new RepositoryItemReaderBuilder<User>()
                .name("mailSenderReader")
                .repository(userRepository)
                .methodName("findAll")
                .pageSize(5)
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("email", Sort.Direction.ASC))
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<User, MailHistory> mailSenderProcessor() {
        return new ItemProcessor<User, MailHistory>() {
            @Override
            public MailHistory process(User user) throws Exception {

                if (userLoginCheck(user)) return null;

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(user.getEmail());
                helper.setSubject("example subject");
                helper.setText("example text");

                File attachment = new File("mailAttachment.txt");
                helper.addAttachment(attachment.getName(), attachment);

                mailSender.send(message);

                int count = (mailHistoryRepository.findAll().size()) + 1;

                MailHistory result = MailHistory.builder()
                        .result("0")
                        .count(count)
                        .email(user.getEmail())
                        .build();

                log.info("Send Success");
                return result;

            }

        };

    }

    @StepScope
    @Bean
    public ItemWriter<MailHistory> mailSenderWriter() {
        return new ItemWriter<MailHistory>() {
            @Override
            public void write(List<? extends MailHistory> items) throws Exception {
                items.forEach(item -> mailHistoryRepository.save(item));
            }
        };
    }

    private boolean userLoginCheck(User user) {
            user.updateStatus();

        if (user.getStatus() == 0) {

            if (user.getEmail().endsWith("@naver.com")) {
                return true;
            }

            user.setLast_login(LocalDateTime.now());
            userRepository.save(user);
        }
        return false;
    }


}
