package com.example.SpringBatchTutorial.core.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class MailSenderScheduler {

    @Autowired
    private Job mailSenderJob;

    @Autowired
    private Job mailSenderDbJob;

    @Autowired
    private JobLauncher jobLauncher;

    //초 분 시 일 월 주
//    @Scheduled(cron = "* * * * * * ")
    @Scheduled(cron = "*/3 * * * * *")
    public void mailSenderJobDbRun() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        JobParameters jobParameters = new JobParameters(
                Collections.singletonMap("requestTime", new JobParameter(System.currentTimeMillis()))
        );

        jobLauncher.run(mailSenderDbJob, jobParameters);
    }

}
