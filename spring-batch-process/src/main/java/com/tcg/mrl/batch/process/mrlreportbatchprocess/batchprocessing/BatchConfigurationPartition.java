package com.tcg.mrl.batch.process.mrlreportbatchprocess.batchprocessing;

import com.tcg.mrl.batch.process.mrlreportbatchprocess.model.BaseOne;
import com.tcg.mrl.batch.process.mrlreportbatchprocess.model.BaseThree;
import com.tcg.mrl.batch.process.mrlreportbatchprocess.model.BaseTwo;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobListenerFactoryBean;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

//@EnableTask
//@EnableRetry
@Configuration
/**
 * Spring Batch with parallel steps and implementing partition in the step one for base one report.
 * @EnableBatchPocessing annotation. This annotation is provided by Spring Batch and is used to bootstrap the batch infrastructure. It provides Spring bean definitions for most of the batch infrastructure so you don’t have to, including JobBuilderFactory StepBuilderFactory
 */
@EnableBatchProcessing
public class BatchConfigurationPartition {

    private static final Integer CHUNK = 10000;
    private static final String EXPORT_FILENAME_BASE_ONE = "D:\\tcg_digital_project\\mrl-report-enterprise\\baseone.xlsx";
    private static final String EXPORT_FILENAME_BASE_TWO = "D:\\tcg_digital_project\\mrl-report-enterprise\\basetwo.xlsx";
    private static final String EXPORT_FILENAME_BASE_THREE = "D:\\tcg_digital_project\\mrl-report-enterprise\\basethree.xlsx";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final DataSource dataSource;

    @Autowired
    public BatchConfigurationPartition(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, EntityManagerFactory entityManagerFactory, DataSource dataSource) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
        this.dataSource = dataSource;
    }

    /**
     * Note JobInstance -> JobInstance can’t be executed more than once to a successful completion and that a JobInstance is identified by the job name and the parameters passed into it
     * spring batch job throw exception if its run with same parameters multiple time to avoid this exception we need to configure JobParametersIncrementer -> RunIdIncrementer is helping to do this.
     * ItemReader
     */
    @Bean
    public Job baseDateProcess(Step stepOne, Step stepTwo, Step stepThree, JobCompletionNotificationListener listener){

        Flow secondFlow = new FlowBuilder<Flow>("secondFlow")
                .start(stepTwo)
                .build();

        Flow thirdFlow = new FlowBuilder<Flow>("thirdFlow")
                .start(stepThree)
                .build();

        Flow parallelFlow = new FlowBuilder<Flow>("parallelFlow")
                .start(stepOne)
                .split(new SimpleAsyncTaskExecutor())
                .add(secondFlow)
                .split(new SimpleAsyncTaskExecutor())
                .add(thirdFlow)
                .build();

        return this.jobBuilderFactory.get("etl-base-data-process")
                .incrementer(new RunIdIncrementer())
                .listener(JobListenerFactoryBean.getListener(listener))
                .start(parallelFlow)
                .end()
                .build();


//        return jobBuilderFactory.get("etl-base-data-process")
//                .incrementer(new RunIdIncrementer())
//                .listener(JobListenerFactoryBean.getListener(listener))
//                .start(stepOne)
////                .next(stepTwo)
//                .build();
    }

    /**
     * Master Step
     */
    @Bean
    public Step stepOne(Step slaveStep, TaskExecutorPartitionHandler partitionHandler) {
        return stepBuilderFactory.get("base-one-master-step-batch-process")
                .partitioner("base-one-slave-step-batch-process", rangePartitioner())
                .partitionHandler(partitionHandler)
                .build();
    }
    @Bean
    public RangePartitioner rangePartitioner() {
        return new RangePartitioner(dataSource);
    }
    @Bean public TaskExecutorPartitionHandler partitionHandler(Step slaveStep) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(slaveStep);
        partitionHandler.setGridSize(2);
        partitionHandler.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return partitionHandler;
    }

    @Bean
    public Step slaveStep(ItemReader<BaseOne> readerBaseOne, ItemWriter<BaseOne> writerBaseOne){
        return stepBuilderFactory.get("base-one-slave-step-batch-process")
                .<BaseOne, BaseOne>chunk(CHUNK)
                .reader(readerBaseOne)
                .writer(writerBaseOne)
                .build();
    }

    @Bean
    public Step stepTwo(ItemReader<BaseTwo> readerBaseTwo, ItemWriter<BaseTwo> writerBaseTwo) {
        return stepBuilderFactory.get("base-two-step-batch-process")
                .<BaseTwo, BaseTwo> chunk(CHUNK)
                .reader(readerBaseTwo)
                .writer(writerBaseTwo)
                .build();
    }
    @Bean
    public Step stepThree(ItemReader<BaseThree> readerBaseThree, ItemWriter<BaseThree> writerBaseThree) {
        return stepBuilderFactory.get("base-three-step-batch-process")
                .<BaseThree, BaseThree> chunk(CHUNK)
                .reader(readerBaseThree)
                .writer(writerBaseThree)
                .build();
    }


    /**
     * How JpaPagingItemReader works-> JpaPagingItemReader provide its own implementation of ItemReader which call the read() method internally. JpaPagingItemReader class doPageRead() read records from DB page size wise
     * then AbstractItemCountingItemStreamItemReader convert that to Pojo which implement ItemReader interface.
     * Note Chunk & Paging size ->
     * Chunk size 10 page size 5 -> If the page size is 5 (paging size) and the chunk size is 10 , in that scenario two times data base select query will hit until the commit count or the chunk size reach , first time it will fetch 5 records after that 5 records total 10 , which is equals to the chunk size then in a single thread item reader will transfer the data to item writer , writer will write all at a once
     * Chunk size 5 page size 10 -> If the page size is 10 (paging size) and the chunk size is 5 , in that scenario 10 records will be fetched from Data base then write the 5 records after that again 5 records will be written because the chunk size is 5.
     * @return
     * @throws Exception
     */
    @StepScope
    @Bean
    public ItemStreamReader<BaseOne> readerBaseOne( @Value("#{stepExecutionContext['fromId']}") int fromId, @Value("#{stepExecutionContext['toId']}") int toId, @Value("#{stepExecutionContext['name']}") final String name,  @Value("#{jobParameters['monthId']}") String monthId, @Value("#{jobParameters['yearId']}") String yearId) throws Exception{
        System.out.println("partition called thread name  - " + name+ "  fromId  "+fromId + "  toId "+ toId);
        String sqlQuery = "SELECT * FROM mrldb.trn_trial_bal_base_1 where mth_id = :monthId and yr_id = :yearId and PARTITION_ID between :fromId and :toId";
//        String sqlQuery = "SELECT * FROM mrldb.trn_trial_bal_base_1_ where mth_id = 01 and yr_id = 2020";
        JpaPagingItemReader<BaseOne> reader = new JpaPagingItemReader<BaseOne>();
        //creating a native query provider as it would be created in configuration
        JpaNativeQueryProvider<BaseOne> queryProvider= new JpaNativeQueryProvider<BaseOne>();
        queryProvider.setSqlQuery(sqlQuery);
        queryProvider.setEntityClass(BaseOne.class);
        queryProvider.afterPropertiesSet();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setPageSize(CHUNK);
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("monthId", monthId);
        parameterValues.put("yearId", yearId);
        parameterValues.put("fromId", fromId);
        parameterValues.put("toId", toId);
        reader.setParameterValues(parameterValues);
        reader.setQueryProvider(queryProvider);
        reader.afterPropertiesSet();
        reader.setSaveState(false); // Indicates of the state of the ItemReader should be saved after each chunk for restartability.  This should be turned to false if used in a multithreaded
        return reader;
    }
    @StepScope
    @Bean
    public ItemStreamReader <BaseTwo> readerBaseTwo(@Value("#{jobParameters['monthId']}") String monthId,  @Value("#{jobParameters['yearId']}") String yearId) throws Exception{
        System.out.println("Item reader call count");
        String sqlQuery = "SELECT * FROM mrldb.trn_trial_bal_base_2_v2 where mth_id = :monthId and yr_id = :yearId";
//        String sqlQuery = "SELECT * FROM mrldb.trn_trial_bal_base_2_v2_sample where mth_id = 01 and yr_id = 2020 ";
        JpaPagingItemReader<BaseTwo> reader = new JpaPagingItemReader<BaseTwo>();
        //creating a native query provider as it would be created in configuration
        JpaNativeQueryProvider<BaseTwo> queryProvider= new JpaNativeQueryProvider<BaseTwo>();
        queryProvider.setSqlQuery(sqlQuery);
        queryProvider.setEntityClass(BaseTwo.class);
        queryProvider.afterPropertiesSet();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setPageSize(CHUNK);
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("monthId", monthId);
        parameterValues.put("yearId", yearId);
        reader.setParameterValues(parameterValues);
        reader.setQueryProvider(queryProvider);
        reader.afterPropertiesSet();
        reader.setSaveState(false); // Indicates of the state of the ItemReader should be saved after each chunk for restartability.  This should be turned to false if used in a multithreaded
        return reader;
    }

    @StepScope
    @Bean
    public ItemStreamReader <BaseThree> readerBaseThree(@Value("#{jobParameters['monthId']}") String monthId, @Value("#{jobParameters['yearId']}") String yearId) throws Exception{
        String sqlQuery = "SELECT * FROM mrldb.fr_mis_base3 where mth_id = :monthId and yr_id = :yearId";
//        String sqlQuery = "SELECT * FROM mrldb.trn_trial_bal_base_2_v2_sample where mth_id = 01 and yr_id = 2020 ";
        JpaPagingItemReader<BaseThree> reader = new JpaPagingItemReader<BaseThree>();
        //creating a native query provider as it would be created in configuration
        JpaNativeQueryProvider<BaseThree> queryProvider= new JpaNativeQueryProvider<BaseThree>();
        queryProvider.setSqlQuery(sqlQuery);
        queryProvider.setEntityClass(BaseThree.class);
        queryProvider.afterPropertiesSet();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setPageSize(CHUNK);
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("monthId", monthId);
        parameterValues.put("yearId", yearId);
        reader.setParameterValues(parameterValues);
        reader.setQueryProvider(queryProvider);
        reader.afterPropertiesSet();
        reader.setSaveState(false); // Indicates of the state of the ItemReader should be saved after each chunk for restartability.  This should be turned to false if used in a multithreaded
        return reader;
    }

    @Bean
    public ItemWriter<BaseOne> writerBaseOne(SXSSFWorkbook workbookBaseOne) {
        SXSSFSheet sheet = workbookBaseOne.createSheet("Base1");
        return new BaseOneItemWriter(sheet);
    }

    @Bean
    public ItemWriter<BaseTwo> writerBaseTwo(SXSSFWorkbook workbookBaseTwo) {
        SXSSFSheet sheet = workbookBaseTwo.createSheet("Base2");
        return new BaseTwoItemWriter(sheet);
    }

    @Bean
    public ItemWriter<BaseThree> writerBaseThree(SXSSFWorkbook workbookBaseThree) {
        SXSSFSheet sheet = workbookBaseThree.createSheet("Base3");
        return new BaseThreeItemWriter(sheet);
    }

    @Bean
    public SXSSFWorkbook workbookBaseOne() {
        return new SXSSFWorkbook(CHUNK);
    }

    @Bean
    public SXSSFWorkbook workbookBaseTwo() {
        return new SXSSFWorkbook(CHUNK);
    }

    @Bean
    public SXSSFWorkbook workbookBaseThree() {
        return new SXSSFWorkbook(CHUNK);
    }

    /**
     * baseDateProcess is dependent upon jobCompletionNotificationListener but in the method parameter the parameter name is listener instead of jobCompletionNotificationListener. For @Bean annotation the bean name is by default the method name in our case is jobCompletionNotificationListener
     * by default for @Bean annotation it will try to resolve the bean my name that is jobCompletionNotificationListener it will not found that name then spring will try to resolve by type , in this type there is only one bean
     * that's why its is getting injected. If there are multiple bean with same type we then have to specify the dependency by name of the bean method
     */
    @Bean
    JobCompletionNotificationListener jobCompletionNotificationListener(SXSSFWorkbook workbookBaseOne, SXSSFWorkbook workbookBaseTwo , SXSSFWorkbook workbookBaseThree, FileOutputStream fileOutputStreamBaseOne, FileOutputStream fileOutputStreamBaseTwo, FileOutputStream fileOutputStreamBaseThree) throws Exception {
        return new JobCompletionNotificationListener(workbookBaseOne, workbookBaseTwo, workbookBaseThree, fileOutputStreamBaseOne, fileOutputStreamBaseTwo, fileOutputStreamBaseThree);
    }

    @Bean
    public FileOutputStream fileOutputStreamBaseOne() throws Exception {
        return new FileOutputStream(EXPORT_FILENAME_BASE_ONE);
    }
    @Bean
    public FileOutputStream fileOutputStreamBaseTwo() throws Exception {
        return new FileOutputStream(EXPORT_FILENAME_BASE_TWO);
    }
    @Bean
    public FileOutputStream fileOutputStreamBaseThree() throws Exception {
        return new FileOutputStream(EXPORT_FILENAME_BASE_THREE);
    }

}
