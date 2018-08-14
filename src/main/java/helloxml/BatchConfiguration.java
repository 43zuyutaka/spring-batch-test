package helloxml;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	public FlatFileItemReader<Food> reader() {
		return new FlatFileItemReaderBuilder<Food>().name("foodItemReader")
				.resource(new ClassPathResource("sample-data3.csv")).delimited()
				.names(new String[] { "foodName", "lovely" }).fieldSetMapper(new BeanWrapperFieldSetMapper<Food>() {
					{
						setTargetType(Food.class);
					}
				}).build();
	}

	@Bean
	public FoodItemProcessor processor() {
		return new FoodItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Food> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Food>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO food (food_name, lovely) VALUES (:foodName, :lovely)").dataSource(dataSource).build();
	}

	@Bean
	public Job importUserJob(JobCompletionNotificationListener2 listener, Step step1) {
		return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer()).listener(listener).flow(step1)
				.end().build();
	}

	@Bean
	public Step step1(JdbcBatchItemWriter<Food> writer) {
		return stepBuilderFactory.get("step1").<Food, Food>chunk(10).reader(reader()).processor(processor())
				.writer(writer).build();
	}
}
