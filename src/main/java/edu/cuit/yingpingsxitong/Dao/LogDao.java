package edu.cuit.yingpingsxitong.Dao;

import edu.cuit.yingpingsxitong.Entity.Log;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LogDao {
    void saveLog(String methodName,String userName);
    List<Log> findAllLog();
}
