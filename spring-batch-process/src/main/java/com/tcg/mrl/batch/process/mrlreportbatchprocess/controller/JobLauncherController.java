package com.tcg.mrl.batch.process.mrlreportbatchprocess.controller;
import com.tcg.mrl.batch.process.mrlreportbatchprocess.service.ReportsCreationService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
public class JobLauncherController {

    private final JobLauncher jobLauncher;
    private final Job job;
    private final ReportsCreationService reportsCreationService;

    @Autowired
    public JobLauncherController(JobLauncher jobLauncher, Job job, ReportsCreationService reportsCreationService){
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.reportsCreationService = reportsCreationService;
    }

    @GetMapping("/launchJob/{monthId}/{yearId}")
    public void launchJob(@PathVariable("monthId") String monthId, @PathVariable("yearId") String yearId){
        System.out.println("job called");
        try{
           /* Map<String, JobParameter> parameters = new HashMap<>();
            parameters.put("monthId", new JobParameter(monthId));
            parameters.put("yearId",  new JobParameter(yearId));
            parameters.put("timestamp",  new JobParameter(new Timestamp(System.currentTimeMillis())));
            JobParameters parameter = new JobParameters(parameters);
            //Job job = this.context.getBean("baseDateProcess", Job.class);
            this.jobLauncher.run(job, parameter).getJobId();*/
            this.reportsCreationService.createReports(monthId, yearId);
            System.out.println("done");

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
