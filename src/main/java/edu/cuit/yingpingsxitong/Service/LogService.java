package edu.cuit.yingpingsxitong.Service;

import edu.cuit.yingpingsxitong.Dao.LogDao;
import edu.cuit.yingpingsxitong.Entity.Log;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class LogService {
    private final LogDao logDao;
    @Autowired
    public LogService(SqlSessionTemplate sqlSessionTemplate) {
        this.logDao = sqlSessionTemplate.getMapper(LogDao.class);
    }

    public void saveLog(String methodName,String userName){
        logDao.saveLog(methodName,userName);
    }

    public List<Log> findAllLog(){
        return logDao.findAllLog();
    }

    public int countLogs() {
        return logDao.countLogs();
    }
}
