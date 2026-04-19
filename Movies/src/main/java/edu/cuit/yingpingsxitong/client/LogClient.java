package edu.cuit.yingpingsxitong.client;

import edu.cuit.yingpingsxitong.Entity.Log;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "movies-service", contextId = "logClient")
public interface LogClient {
    @PostMapping("/rpc/log/save")
    Result<Void> saveLog(@RequestParam("methodName") String methodName, @RequestParam("userName") String userName);

    @GetMapping("/rpc/log/findAll")
    Result<List<Log>> findAllLog();

    @GetMapping("/rpc/log/count")
    Result<Integer> countLogs();
}
