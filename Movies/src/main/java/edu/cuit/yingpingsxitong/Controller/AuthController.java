package edu.cuit.yingpingsxitong.Controller;

import edu.cuit.yingpingsxitong.Entity.User;
import edu.cuit.yingpingsxitong.Service.UserService;
import edu.cuit.yingpingsxitong.client.Result;
import edu.cuit.yingpingsxitong.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一认证控制器
 *
 * 功能：
 * 1. 提供 /auth/login 接口：验证用户名密码，返回 JWT Token
 * 2. 提供 /auth/register 接口：注册新用户，返回 JWT Token
 * 3. 供网关层调用，实现微服务的统一认证与授权
 *
 * 使用方式：
 * - 前端调用 /auth/login 获取 token
 * - 后续请求在 Header 中携带：Authorization: Bearer <token>
 * - 网关层的 AuthFilter 会验证该 token
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 登录接口 - 返回 JWT Token
     *
     * POST /auth/login
     * Body: username=xxx&password=xxx
     */
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestParam("username") String username,
                                            @RequestParam("password") String password) {
        User user = userService.findUserByUsername(username);

        if (user == null) {
            return Result.error("用户不存在");
        }

        if (!user.getPassword().equals(password)) {
            return Result.error("密码错误");
        }

        if (!user.getPermission()) {
            return Result.error("账号已被禁用");
        }

        // 生成 JWT Token
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());

        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("username", user.getUsername());
        data.put("userId", String.valueOf(user.getUserId()));
        data.put("manager", String.valueOf(user.getManager()));

        return Result.success(data);
    }

    /**
     * 注册接口 - 注册成功后返回 JWT Token
     *
     * POST /auth/register
     * Body: username=xxx&password=xxx&email=xxx
     */
    @PostMapping("/register")
    public Result<Map<String, String>> register(@RequestParam("username") String username,
                                               @RequestParam("password") String password,
                                               @RequestParam("email") String email) {
        // 检查用户名是否已存在
        if (userService.findUserByUsername(username) != null) {
            return Result.error("用户名已存在");
        }

        User user = new User(username, password, email);
        userService.insertUser(user);

        // 重新查询获取生成的 userId
        User savedUser = userService.findUserByUsername(username);
        String token = jwtUtil.generateToken(savedUser.getUserId(), savedUser.getUsername());

        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("username", savedUser.getUsername());
        data.put("userId", String.valueOf(savedUser.getUserId()));

        return Result.success(data);
    }

    /**
     * Token 验证测试接口
     *
     * GET /auth/verify
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/verify")
    public Result<Map<String, String>> verify(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        if (!jwtUtil.validateToken(token)) {
            return Result.error("Token 无效或已过期");
        }

        Map<String, String> data = new HashMap<>();
        data.put("userId", String.valueOf(jwtUtil.getUserIdFromToken(token)));
        data.put("username", jwtUtil.getUsernameFromToken(token));
        data.put("status", "有效");

        return Result.success(data);
    }
}
