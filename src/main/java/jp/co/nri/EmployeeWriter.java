package jp.co.nri;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import jp.co.nri.mapping.EmployeeData;

@Component("itemWriter")
public class EmployeeWriter implements ItemWriter<EmployeeData> {
	private static final Log log = LogFactory.getLog(EmployeeWriter.class);

	@Override
	public void write(List<? extends EmployeeData> items) throws Exception {
		// TODO Auto-generated method stub
		for(EmployeeData emp: items) {
			log.info(emp.getId() + ":" + emp.getName() + "/" + emp.getNote());
		}
		
	}

}
