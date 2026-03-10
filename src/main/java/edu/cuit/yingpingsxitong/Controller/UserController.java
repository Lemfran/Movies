package edu.cuit.yingpingsxitong.Controller;

import edu.cuit.yingpingsxitong.Entity.User;
import edu.cuit.yingpingsxitong.Service.UserService;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@SessionAttributes("user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping({"/login","/"})
    public String login(Model model){
        return "login";
    }

    // 处理根URL的请求，返回"register"视图
    @RequestMapping("/register") // 使用@RequestMapping注解映射根URL到这个方法
    public String register() {
        return "register"; // 返回视图名称
    }

    @RequestMapping("/regist1") // 使用@RequestMapping注解映射注册URL到这个方法
    public String registUser1(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              @RequestParam("email") String email,
                              Model model) { // 模型对象，用于向视图传递数据
        User user = new User(username, password ,email); // 创建User对象
        userService.insertUser(user);
        model.addAttribute("user", user); // 将User对象添加到模型中
        return "zcsuccess"; // 返回视图名称
    }
}
