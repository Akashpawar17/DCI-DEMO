package com.capg.demo;

import java.io.Reader;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;



@SpringBootApplication
@EnableBatchProcessing
public class SingleBatchJobApplication {
@Autowired
JobBuilderFactory jobBuilderFactory;
@Autowired
StepBuilderFactory stepBuilderFactory;



@Value("classPath:/csv/doc*.csv")
private Resource[] csvResource1;



private Resource outputResource = new FileSystemResource("/Single-batch-job/src/main/resources/csv/output.csv");

@Bean
public Job executeJob1() throws Exception {


	return jobBuilderFactory.get("job1").incrementer(new RunIdIncrementer()).flow(step1()).end().build();
}

@Bean
public Step step1() throws Exception {

	return stepBuilderFactory.get("step1").<File1, File1>chunk(2).reader(multiResourceItemReader())
		.writer(writer()).
		
			build();
}
@Bean
public MultiResourceItemReader<File1> multiResourceItemReader() 
{
    MultiResourceItemReader<File1> resourceItemReader = new MultiResourceItemReader<File1>();
    resourceItemReader.setResources(csvResource1);
    resourceItemReader.setDelegate(reader1());
    return resourceItemReader;
}

public FlatFileItemReader<File1> reader1() {
	FlatFileItemReader<File1> itemReader = new FlatFileItemReader<>();
	itemReader.setLineMapper(lineMapper1());
	itemReader.setLinesToSkip(1);
	return itemReader;
}

public LineMapper<File1> lineMapper1() {
	DefaultLineMapper<File1> lineMapper = new DefaultLineMapper<>();
	DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
	lineTokenizer.setNames(new String[] { "aid", "aname","value" });
	lineTokenizer.setIncludedFields(new int[] { 0, 1, 2});
	BeanWrapperFieldSetMapper<File1> fieldSetMapper = new BeanWrapperFieldSetMapper<File1>();
	fieldSetMapper.setTargetType(File1.class);
	lineMapper.setLineTokenizer(lineTokenizer);
	lineMapper.setFieldSetMapper(fieldSetMapper);
	return lineMapper;
}
@Bean
public FlatFileItemWriter<File1> writer() 
{
    //Create writer instance
    FlatFileItemWriter<File1> writer = new FlatFileItemWriter<>();
     
    //Set output file location
    writer.setResource(outputResource);
     
    writer.setAppendAllowed(true);
  

    writer.setLineAggregator(new DelimitedLineAggregator<File1>() {
        {
            setDelimiter(",");
            setFieldExtractor(new BeanWrapperFieldExtractor<File1>() {
                {
                    setNames(new String[] { "aid", "aname", "value" });
                }
            });
        }
    });
    return writer;
}




	public static void main(String[] args) {
		SpringApplication.run(SingleBatchJobApplication.class, args);
	}

}
