package ma.ensa.config;

import ma.ensa.batch.BatchLauncher;
import ma.ensa.batch.PersonProcessor;
import ma.ensa.batch.PersonWriter;
import ma.ensa.entities.Personne;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
@EnableBatchProcessing
@Configuration

@EnableJpaRepositories("ma.ensa.repositories")
@EntityScan("ma.ensa.entities")
@EnableScheduling

public class AppConfig {

    @Value("personnes.txt")
    private Resource inputResource;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

 /*  @Autowired
   public DataSource dataSource;
*/

    //@Autowired
    //PlatformTransactionManager dataSourceTransactionManager;


    @Bean
    public FlatFileItemReader<Personne> reader() {
        FlatFileItemReader<Personne> itemReader = new FlatFileItemReader<Personne>();
        itemReader.setLineMapper(lineMapper());
        //itemReader.setLinesToSkip(1);
        itemReader.setResource(inputResource);
        return itemReader;
    }

    @Bean
    public LineMapper<Personne> lineMapper() {
        DefaultLineMapper<Personne> lineMapper = new DefaultLineMapper<Personne>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(new String[] { "id", "nom", "prenom","civilite" });
        lineTokenizer.setDelimiter(",");
        //   lineTokenizer.setIncludedFields(new int[] { 0, 1, 2 });
        BeanWrapperFieldSetMapper<Personne> fieldSetMapper = new BeanWrapperFieldSetMapper<Personne>();
        fieldSetMapper.setTargetType(Personne.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public ItemWriter<Personne> writer() {
        return new PersonWriter();
    }

    @Bean
    public ItemProcessor<Personne, Personne> processor() {
        return new PersonProcessor();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<Personne, Personne>chunk(2)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean(name = "importPersons")
    public Job importTransactions(JobBuilderFactory jobs) {
        return jobs.get("importPersons")
                .start(step1())
                .build();
    }

    @Bean
    public JobRepository jobRepository() throws Exception {
        ResourcelessTransactionManager transactionManager = new ResourcelessTransactionManager();
        MapJobRepositoryFactoryBean jobRepository =  new MapJobRepositoryFactoryBean(transactionManager);
        //jobRepository.afterPropertiesSet();
        JobRepository jobRepository1 = jobRepository.getObject();
        return jobRepository1;
    }


/*

    @Bean(name = "transactionJobRepository")
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(dataSourceTransactionManager);
        return factory.getObject();
    }
*/


   @Bean//(name = "jobLauncher")
    public JobLauncher jobLauncher() throws Exception {

        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository());
        return jobLauncher;
    }

    @Bean
    public BatchLauncher launchBatch() {
        return new BatchLauncher();
    }

    /*@Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(20);
        return threadPoolTaskExecutor;
    }*/


   @Scheduled(cron = "0,30 * * * * *")
    public void scheduleFixedDelayTask() throws
           JobExecutionAlreadyRunningException,
           JobRestartException,
           JobInstanceAlreadyCompleteException,
           JobParametersInvalidException {
           launchBatch().run();
    }

}
