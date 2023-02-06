package org.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.example.reggie.common.R;
import org.example.reggie.entity.Employee;
import org.example.reggie.service.EmployeeService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

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

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> Save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工 {} 员工信息",employee.toString());
        //获取身份证号后六位
        String idNumber = employee.getIdNumber().substring(employee.getIdNumber().length()-6);
        log.info("密码：{}",idNumber);
        //设置初始密码为身份证后六位，需要进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(idNumber.getBytes(StandardCharsets.UTF_8)));
        //设置时间
        //第一种方法
        /*Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(date);
        log.info("时间：{}",time);*/
        //第二种方法(1.8及之后开始支持)
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //获取当前用户的id
        Long empId = (Long) request.getSession().getAttribute("employee");
        //创建该员工的用户，及当前登录操作的用户
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        //保存
        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造条件的构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        //获取当前登录用户id
        Long empId = (Long) request.getSession().getAttribute("employee");
        log.info(employee.toString());
        //设置更新此账号的用户
        employee.setUpdateUser(empId);
        //设置更新时间
        employee.setUpdateTime(LocalDateTime.now());
        //更新操作
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }
}
