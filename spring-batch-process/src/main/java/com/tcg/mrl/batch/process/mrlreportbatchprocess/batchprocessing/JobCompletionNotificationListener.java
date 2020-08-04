package com.tcg.mrl.batch.process.mrlreportbatchprocess.batchprocessing;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

import java.io.FileOutputStream;


public class JobCompletionNotificationListener {

    private final SXSSFWorkbook workbookBaseOne;
    private final SXSSFWorkbook workbookBaseTwo;
    private final SXSSFWorkbook workbookBaseThree;
    private final FileOutputStream fileOutputStreamBaseOne;
    private final FileOutputStream fileOutputStreamBaseTwo;
    private final FileOutputStream fileOutputStreamBaseThree;

    public JobCompletionNotificationListener(SXSSFWorkbook workbookBaseOne, SXSSFWorkbook workbookBaseTwo, SXSSFWorkbook workbookBaseThree, FileOutputStream fileOutputStreamBaseOne,  FileOutputStream fileOutputStreamBaseTwo, FileOutputStream fileOutputStreamBaseThree) {
        this.workbookBaseOne = workbookBaseOne;
        this.workbookBaseTwo = workbookBaseTwo;
        this.workbookBaseThree = workbookBaseThree;
        this.fileOutputStreamBaseOne = fileOutputStreamBaseOne;
        this.fileOutputStreamBaseTwo = fileOutputStreamBaseTwo;
        this.fileOutputStreamBaseThree = fileOutputStreamBaseThree;
    }

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("job starting------------------");
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            System.out.println("!!! JOB FINISHED! Time to verify the results");
            try {
                workbookBaseOne.write(fileOutputStreamBaseOne);
                workbookBaseTwo.write(fileOutputStreamBaseTwo);
                workbookBaseThree.write(fileOutputStreamBaseThree);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try{
                    fileOutputStreamBaseOne.close();
                    fileOutputStreamBaseTwo.close();
                    fileOutputStreamBaseThree.close();
                    workbookBaseOne.dispose();
                    workbookBaseTwo.dispose();
                    workbookBaseThree.dispose();
                }catch (Exception r){
                    System.out.println("Error while closing resource "+ r);
                }
            }
        }else{
            System.out.println("JOB is unsuccessful!!!!");
        }
    }

}
