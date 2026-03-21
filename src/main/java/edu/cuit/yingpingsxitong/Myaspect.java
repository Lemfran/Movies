package edu.cuit.yingpingsxitong;

import edu.cuit.yingpingsxitong.Entity.User;
import edu.cuit.yingpingsxitong.Service.LogService;
import edu.cuit.yingpingsxitong.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Component
@Aspect
public class Myaspect {
    @Autowired
    private LogService logService;
    @Autowired
    private UserService userService;

    // 处理其他 Controller（非 IndexController 和 UserController）
    // 从 Session 获取用户名
    @After(value = "execution(* edu.cuit.yingpingsxitong.Controller.*..*(..))&& !execution(* edu.cuit.yingpingsxitong.Controller.IndexController.*(..))&& !execution(* edu.cuit.yingpingsxitong.Controller.UserController.*(..))")
    public void after(JoinPoint joinPoint){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request =(HttpServletRequest)requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        assert request != null;

        // 从 Session 获取用户
        User user = (User) request.getSession().getAttribute("user");
        String methodName = joinPoint.getSignature().getName(); // 获取目标方法名

        if (user != null && user.getUsername() != null) {
            logService.saveLog(methodName, user.getUsername());
        }
    }

    // 处理 IndexController 的 login 方法
    @After(value = "execution(* edu.cuit.yingpingsxitong.Controller.IndexController.login(..))")
    public void after2(JoinPoint joinPoint){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request =(HttpServletRequest)requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        assert request != null;
        String username = request.getParameter("username");
        String methodName = joinPoint.getSignature().getName(); // 获取目标方法名
        if (username != null && !username.isEmpty()) {
            logService.saveLog(methodName, username);
        }
    }

    // 处理 IndexController 的 home 方法
    @After(value = "execution(* edu.cuit.yingpingsxitong.Controller.IndexController.home(..))")
    public void after3(JoinPoint joinPoint){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request =(HttpServletRequest)requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        assert request != null;
        String methodName = joinPoint.getSignature().getName(); // 获取目标方法名

        // 从 Session 获取用户名
        User user = (User) request.getSession().getAttribute("user");
        if (user != null && user.getUsername() != null) {
            logService.saveLog(methodName, user.getUsername());
        }
    }
}
