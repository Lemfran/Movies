package edu.cuit.yingpingsxitong;

import edu.cuit.yingpingsxitong.Entity.User;
import edu.cuit.yingpingsxitong.client.LogClient;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Component
@Aspect
public class Myaspect {
    @Autowired
    private LogClient logClient;

    @After(value = "execution(* edu.cuit.yingpingsxitong.Controller.*..*(..))&& !execution(* edu.cuit.yingpingsxitong.Controller.IndexController.*(..))&& !execution(* edu.cuit.yingpingsxitong.Controller.UserController.*(..))&& !execution(* edu.cuit.yingpingsxitong.Controller.*RpcController.*(..))")
    public void after(JoinPoint joinPoint){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = (HttpServletRequest)requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        assert request != null;

        User user = (User) request.getSession().getAttribute("user");
        String methodName = joinPoint.getSignature().getName();

        if (user != null && user.getUsername() != null) {
            logClient.saveLog(methodName, user.getUsername());
        }
    }

    @After(value = "execution(* edu.cuit.yingpingsxitong.Controller.IndexController.login(..))")
    public void after2(JoinPoint joinPoint){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = (HttpServletRequest)requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        assert request != null;
        String username = request.getParameter("username");
        String methodName = joinPoint.getSignature().getName();
        if (username != null && !username.isEmpty()) {
            logClient.saveLog(methodName, username);
        }
    }

    @After(value = "execution(* edu.cuit.yingpingsxitong.Controller.IndexController.home(..))")
    public void after3(JoinPoint joinPoint){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = (HttpServletRequest)requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        assert request != null;
        String methodName = joinPoint.getSignature().getName();

        User user = (User) request.getSession().getAttribute("user");
        if (user != null && user.getUsername() != null) {
            logClient.saveLog(methodName, user.getUsername());
        }
    }
}
