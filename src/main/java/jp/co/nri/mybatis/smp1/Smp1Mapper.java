package jp.co.nri.mybatis.smp1;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import jp.co.nri.mapping.EmployeeData;

public interface Smp1Mapper {
	@Select("SELECT * FROM employee WHERE id ='id001'")
	 EmployeeData getUser(@Param("userId") String userId);
}
