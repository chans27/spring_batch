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
        log.info("mailSenderDbJob 실행");
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
        log.info("메일센더리더 실행 ");
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
        log.info("메일센더프로세서 실행");
        return new ItemProcessor<User, MailHistory>() {
            @Override
            public MailHistory process(User user) throws Exception {


                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(user.getEmail());
                helper.setSubject("배치예시");
                helper.setText("예시 본문입니다.");

                File attachment = new File("mailAttachment.txt");
                helper.addAttachment(attachment.getName(), attachment);

//                mailSender.send(message);

                int count = (mailHistoryRepository.findAll().size()) + 1;

                MailHistory result = MailHistory.builder()
                        .result("0")
                        .count(count)
                        .build();

                return result;

            }
        };
    }


    @StepScope
    @Bean
    public ItemWriter<MailHistory> mailSenderWriter() {
        log.info("메일센더라이터 실행");
        return new ItemWriter<MailHistory>() {
            @Override
            public void write(List<? extends MailHistory> items) throws Exception {
//                items.forEach(item -> mailHistoryRepository.save(item));
            }
        };
    }

}
