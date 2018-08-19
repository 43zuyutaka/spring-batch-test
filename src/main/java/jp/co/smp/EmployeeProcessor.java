package jp.co.smp;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import jp.co.smp.mapping.EmployeeData;

@Component("itemProcessor")
public class EmployeeProcessor implements ItemProcessor<EmployeeData, EmployeeData> {

	@Override
	public EmployeeData process(EmployeeData item) throws Exception {
		// TODO Auto-generated method stub
		EmployeeData data = new EmployeeData();
        data.setId(item.getId());
        data.setName(item.getName() + "さん");
        data.setNote(item.getNote() + "以上です!!");
        return data;
	}

}
