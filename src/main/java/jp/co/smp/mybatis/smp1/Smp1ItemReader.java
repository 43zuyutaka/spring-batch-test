package jp.co.smp.mybatis.smp1;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.smp.mapping.EmployeeData;

@Component("smp1ItemReader")
public class Smp1ItemReader implements ItemReader<EmployeeData> {
	private boolean flg = false;
	
	@Autowired
	private Smp1Mapper employeeMapper;
	
	@Override
	public EmployeeData read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		System.out.println("*** employee Reader start ***");
		EmployeeData data = employeeMapper.getUser("user001");
		if (flg == false) {
			flg = true;
			System.out.println("*** employee Reader done 111 ***");
			return data;
		} else {
			System.out.println("*** employee Reader done 222 ***");
			return null;
		}
	}

//	@Override
//	public EmployeeData read()
//			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
//		// TODO Auto-generated method stub
//		EmployeeData data = new EmployeeData();
//		data.setId("id001");
//		data.setName("name 001★");
//		data.setNote("note 001");
//		if (flg == false) {
//			flg = true;
//			return data;
//		} else {
//			return null;
//		}
//	}
}
