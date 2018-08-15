package jp.co.nri;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import jp.co.nri.mapping.EmployeeData;

@Component("itemWriter")
public class EmployeeWriter implements ItemWriter<EmployeeData> {
	private static final Logger log = LoggerFactory.getLogger(EmployeeWriter.class);

	@Override
	public void write(List<? extends EmployeeData> items) throws Exception {
		// TODO Auto-generated method stub
		for(EmployeeData emp: items) {
			log.info(emp.getId() + ":" + emp.getName() + "/" + emp.getNote());
		}
		
	}

}
