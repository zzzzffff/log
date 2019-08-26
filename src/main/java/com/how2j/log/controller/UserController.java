package com.how2j.log.controller;


import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.how2j.log.domain.User;
import com.how2j.log.domapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@Controller
public class UserController {

    @Autowired(required = false) UserMapper userMapper;

    @Autowired(required = false)
    DefaultKaptcha defaultKaptcha;



    @RequestMapping("goregister")//去注册页面
    public String goregister(){
        return  "register";
    }

    @RequestMapping("goeditPassword")//去修改页面
    public String goeditPassword(){
        return  "editPassword";
    }




    @RequestMapping("login")//登陆
    public String login(){
        return "login";
    }


    @RequestMapping("editPassword")
    @ResponseBody
    public boolean editPassword(User u){
        int a = 0;
        List<User> users = userMapper.findAll();
        for ( User user: users) {
            if (u.getName().equals(user.getName())&&u.getPassword().equals(user.getPassword())){

                 a = userMapper.update(u);
            }

        }
        if (a>0){
            return true;

        }else{
            return false;
        }



    }


    @RequestMapping("register")
    @ResponseBody
    public int register(User user,HttpServletRequest httpServletRequest){
        if (user.getName()==null || user.getPassword()==null){
            return 2;
        }
        int i = 0;
        String captchaId = (String) httpServletRequest.getSession().getAttribute("vrifyCode");
        if (captchaId.equalsIgnoreCase(user.getCode())){
             i = userMapper.save(user);
        }else{
            return 3;
        }
        if (i>0){
            return 1;
        }else {
            return 2;
        }

    }


    @RequestMapping("gologin")
    @ResponseBody
    public int login(User u,HttpServletRequest httpServletRequest) {
        List<User> users = userMapper.findAll();
        String code = (String) httpServletRequest.getSession().getAttribute("vrifyCode");
        for (int i=0;i<users.size();i++){
            User user=users.get(i);
            if ((u.getName().equals(user.getName()))&&(u.getPassword().equals(user.getPassword()))){
                if (code.equalsIgnoreCase(u.getCode())){
                    return 1;
                }else{
                    return 3;
                }

            }
        }
        return 2;
    }


    //获取验证码
    @RequestMapping("getcode")
    public void defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception{
        byte[] captchaChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            //生产验证码字符串并保存到session中
            String createText = defaultKaptcha.createText();
            httpServletRequest.getSession().setAttribute("vrifyCode", createText);
            //使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
            BufferedImage challenge = defaultKaptcha.createImage(createText);
            ImageIO.write(challenge, "jpg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        //定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream =
                httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }


}
