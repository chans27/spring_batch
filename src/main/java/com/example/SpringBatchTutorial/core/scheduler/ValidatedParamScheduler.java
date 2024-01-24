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
public class ValidatedParamScheduler {

    @Autowired
    private Job validatedParamJob;

    @Autowired
    private JobLauncher jobLauncher;

    @Scheduled(cron = "*/3 * * * * * ")
    public void validatedParamJobRun() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        JobParameters jobParameters = new JobParameters(
                Collections.singletonMap("validatedParamJob", new JobParameter(System.currentTimeMillis()))
        );

//        jobLauncher.run(validatedParamJob, jobParameters);
    }
}
