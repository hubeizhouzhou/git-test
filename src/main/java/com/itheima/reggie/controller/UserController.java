package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.Utils.ValidateCodeUtils;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /*
    发送手机短信验证码
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user,HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成验证码
           String code = ValidateCodeUtils.generateValidateCode(4).toString();
           //log.info("code={}",code);
            //调用阿里云提供的短信服务
         //   SMSUtils.sendMessage("瑞吉外卖","",phone,code);
            //将生成的验证码保存到session
            session.setAttribute(phone,code);
            return R.success("手机验证码发送成功");
        }

        return R.error("验证码发送失败");
    }

    /*
    移动端用户登录
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession httpSession){
        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从session中获取保存的验证码
        Object codeInSession = httpSession.getAttribute(phone);
        //验证码比对
        if (codeInSession !=null && codeInSession.equals(code)){
        //判断是否为新用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if(user ==null){
            //新用户自动注册
                user =new User();
                user.setPhone(phone);
                userService.save(user);
            }
            httpSession.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
