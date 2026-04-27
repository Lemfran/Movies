package edu.cuit.yingpingsxitong.client;

import edu.cuit.yingpingsxitong.Entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "movies-service", contextId = "userClient")
public interface UserClient {
    @PostMapping("/rpc/user/insert")
    Result<Void> insertUser(@RequestBody User user);

    @GetMapping("/rpc/user/findByUsername")
    Result<User> findUserByUsername(@RequestParam("username") String username);

    @GetMapping("/rpc/user/findById")
    Result<User> findUserById(@RequestParam("id") Integer id);

    @PostMapping("/rpc/user/update")
    Result<Void> updateUser(@RequestBody User user);

    @PostMapping("/rpc/user/delete")
    Result<Void> deleteUser(@RequestParam("userId") Integer userId);

    @GetMapping("/rpc/user/findAll")
    Result<List<User>> findAllUsers();

    @GetMapping("/rpc/user/count")
    Result<Integer> countUsers();

    @PostMapping("/rpc/user/updatePermission")
    Result<Void> updatePermission(@RequestParam("userId") Integer userId, @RequestParam("permission") Boolean permission);

    @PostMapping("/rpc/user/updateManager")
    Result<Void> updateManager(@RequestParam("userId") Integer userId, @RequestParam("manager") Boolean manager);

    @PostMapping("/rpc/user/validatePassword")
    Result<Boolean> validatePassword(@RequestParam("username") String username, @RequestParam("password") String password);
}
