package com.example.SpringBatchTutorial.job.MailSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MailSenderConfig {

    private final JavaMailSender mailSender;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job mailSenderJob(Step mailSenderStep) {
        return jobBuilderFactory.get("mailSenderJob")
                .incrementer(new RunIdIncrementer())
                .start(mailSenderStep)
                .build();
    }

    @JobScope
    @Bean
    public Step mailSenderStep(Tasklet mailSenderTasklet) {
        return stepBuilderFactory.get("mailSenderStep")
                .tasklet(mailSenderTasklet)
                .build();
    }

    @StepScope
    @Bean
    public Tasklet mailSenderTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                sendEmailWithAttachment("everever1275@gmail.com", "mail subject", "mail text");
                return RepeatStatus.FINISHED;
            }
        };
    }

    //참고2
    public void sendEmailWithAttachment(String receiverEmail, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(receiverEmail); //
        helper.setSubject(subject);
        helper.setText(body);

        mailSender.send(message);
    }

}
