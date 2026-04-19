package edu.cuit.yingpingsxitong.Controller;

import edu.cuit.yingpingsxitong.Entity.Log;
import edu.cuit.yingpingsxitong.Service.LogService;
import edu.cuit.yingpingsxitong.client.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 日志服务RPC控制器 - 提供日志相关的RPC接口
 */
@RestController
@RequestMapping("/rpc/log")
public class LogRpcController {

    @Autowired
    private LogService logService;

    @PostMapping("/save")
    public Result<Void> saveLog(@RequestParam("methodName") String methodName,
                                 @RequestParam("userName") String userName) {
        logService.saveLog(methodName, userName);
        return Result.success();
    }

    @GetMapping("/findAll")
    public Result<List<Log>> findAllLog() {
        List<Log> logs = logService.findAllLog();
        return Result.success(logs);
    }

    @GetMapping("/count")
    public Result<Integer> countLogs() {
        return Result.success(logService.countLogs());
    }
}
