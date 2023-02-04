package org.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.R;
import org.example.reggie.entity.Employee;
import org.example.reggie.service.EmployeeService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController{
    @Resource
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){
        //1.将页面提交的密码password进行MD5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //Employee::getUsername 获取方法引用，转换为属性名信息，通过截取方法名得到属性名与实体类属性名作比较，转化之后相当于得到数据库字段
        //如果配置设置了驼峰，将根据实体类的属性名映射出字段名，因此和数据库字段名必须一致或者是其与数据库字段的驼峰名
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果没有查询到，则返回登录失败结果
        if (emp==null){
            return R.error("登陆失败");
        }

        //4.密码比对，如果不一致，则返回登陆失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("登陆失败");
        }

        //5.查看员工状态，如果已禁用状态，则返回员工已禁用结果
        if (emp.getStatus()==0){
            return R.error("账号已禁用");
        }

        //6.登陆成功，将员工id存入Session并返回登陆成功结果
        HttpSession session = request.getSession();
        session.setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 用户退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute("employee");
        return R.success("退出成功");
    }
}
