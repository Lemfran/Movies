package edu.cuit.yingpingsxitong;

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

    //@After(value = "execution(* edu.cuit.yingpingsxitong.Service.*..*(..)) && !execution(* edu.cuit.yingpingsxitong.Service.LogService.*(..))")
    @After(value = "execution(* edu.cuit.yingpingsxitong.Controller.*..*(..))&& !execution(* edu.cuit.yingpingsxitong.Controller.IndexController.*(..))&& !execution(* edu.cuit.yingpingsxitong.Controller.UserController.*(..))")
    public void after(JoinPoint joinPoint){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request =(HttpServletRequest)requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        assert request != null;
        Integer userId = Integer.valueOf(request.getParameter("userId"));
        String methodName = joinPoint.getSignature().getName(); // 获取目标方法名
        logService.saveLog(methodName,userService.findUserById(userId).getUsername());
    }

    @After(value = "execution(* edu.cuit.yingpingsxitong.Controller.IndexController.*(..))")
    public void after2(JoinPoint joinPoint){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request =(HttpServletRequest)requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        assert request != null;
        String username = request.getParameter("username");
        String methodName = joinPoint.getSignature().getName(); // 获取目标方法名
        logService.saveLog(methodName,username);
    }
}
