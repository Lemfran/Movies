package edu.cuit.yingpingsxitong.Controller;

import edu.cuit.yingpingsxitong.Entity.User;
import edu.cuit.yingpingsxitong.Service.UserService;
import edu.cuit.yingpingsxitong.client.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 用户服务RPC控制器 - 提供用户相关的RPC接口
 */
@RestController
@RequestMapping("/rpc/user")
public class UserRpcController {

    @Autowired
    private UserService userService;

    @PostMapping("/insert")
    public Result<Void> insertUser(@RequestBody User user) {
        if (user.getPassword() != null) {
            user.setPassword(userService.encodePassword(user.getPassword()));
        }
        userService.insertUser(user);
        return Result.success();
    }

    @PostMapping("/validatePassword")
    public Result<Boolean> validatePassword(@RequestParam("username") String username,
                                             @RequestParam("password") String password) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return Result.success(false);
        }
        return Result.success(userService.validatePassword(password, user.getPassword()));
    }

    @GetMapping("/findByUsername")
    public Result<User> findUserByUsername(@RequestParam("username") String username) {
        User user = userService.findUserByUsername(username);
        return Result.success(user);
    }

    @GetMapping("/findById")
    public Result<User> findUserById(@RequestParam("id") Integer id) {
        User user = userService.findUserById(id);
        return Result.success(user);
    }

    @PostMapping("/update")
    public Result<Void> updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<Void> deleteUser(@RequestParam("userId") Integer userId) {
        userService.deleteUser(userId);
        return Result.success();
    }

    @GetMapping("/findAll")
    public Result<List<User>> findAllUsers() {
        List<User> users = userService.findAllUsers();
        return Result.success(users);
    }

    @GetMapping("/count")
    public Result<Integer> countUsers() {
        return Result.success(userService.countUsers());
    }

    @PostMapping("/updatePermission")
    public Result<Void> updatePermission(@RequestParam("userId") Integer userId,
                                          @RequestParam("permission") Boolean permission) {
        userService.updatePermission(userId, permission);
        return Result.success();
    }

    @PostMapping("/updateManager")
    public Result<Void> updateManager(@RequestParam("userId") Integer userId,
                                       @RequestParam("manager") Boolean manager) {
        userService.updateManager(userId, manager);
        return Result.success();
    }
}
