package jp.co.smp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
@Component
public class TaskletSample implements Tasklet {
	private Logger logger = LoggerFactory.getLogger(TaskletSample.class);

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// TODO Auto-generated method stub
		logger.info("★taskletのサンプル");
		return RepeatStatus.FINISHED;
	}

}
