package jp.co.smp.mybatis.smp1;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import jp.co.smp.mapping.EmployeeData;

@Component("smp1ItemProcessor")
public class EmployeeProcessor implements ItemProcessor<EmployeeData, EmployeeData> {

	@Override
	public EmployeeData process(EmployeeData item) throws Exception {
		
		
		EmployeeData data = new EmployeeData();
        data.setId(item.getId());
        data.setName(item.getName() + "さん");
        data.setNote(item.getNote() + "以上です!!");
        return data;
	}

}
