package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /*
    员工登录
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将页面提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果没有查询到则返回登录失败结果
        if (emp == null){
            return R.error("登录失败，账号不存在");
        }

       //4.密码对比，如果不一致则返回登录失败
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败，密码错误");
        }

        //5.查看员工状态是否被禁用
        if(emp.getStatus() == 0){
            return R.error("登录失败，您的账号已被禁用");
        }

        //6.登陆成功，将员工id存入session
        request.getSession().setAttribute("employee",emp.getId());
        return  R.success(emp);
    }

    //员工退出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
       // log.info("新增员工，员工信息:{}",employee.toString());
        //设置初始密码为123456，需要md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
       // employee.setCreateTime(LocalDateTime.now());
       // employee.setUpdateTime(LocalDateTime.now());

        //获得当前用户id
      //  Long empID = (Long) request.getSession().getAttribute("employee");
       // employee.setCreateUser(empID);
       // employee.setUpdateUser(empID);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    //分页功能
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        // log.info("page = {},pageSize = {}, name= {}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> QueryWrapper= new LambdaQueryWrapper();

        //添加过滤条件  isNotEmpty 不为空
        QueryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName,name);

        //添加排序条件
        QueryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,QueryWrapper);

        return R.success(pageInfo);
    }

    /*
    根据id修改员工信息
     */
    @PutMapping
    public R<String> update(HttpServletRequest request ,@RequestBody Employee employee){
     //   Long empID = (Long)request.getSession().getAttribute("employee");
      //  employee.setUpdateTime(LocalDateTime.now());
     //   employee.setUpdateUser(empID);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if(employee != null){
        return R.success(employee);
    }
        return R.error("鼠鼠没有找到这个员工诶");
    }
}
