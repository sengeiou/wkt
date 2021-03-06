package com.zmj.wkt.jwt;

/**
 * code is far away from bug with the animal protecting
 * ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　┳┛　┗┳　┃
 * ┃　　　　　　　┃
 * ┃　　　┻　　　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 * 　　┃　　　┃神兽保佑
 * 　　┃　　　┃代码无BUG！
 * 　　┃　　　┗━━━┓
 * 　　┃　　　　　　　┣┓
 * 　　┃　　　　　　　┏┛
 * 　　┗┓┓┏━┳┓┏┛
 * 　　　┃┫┫　┃┫┫
 * 　　　┗┻┛　┗┻┛
 *
 * @author : zmj
 * @description :
 * ---------------------------------
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zmj.wkt.common.RestfulResult;
import com.zmj.wkt.common.exception.CommonException;
import com.zmj.wkt.entity.Bs_permission;
import com.zmj.wkt.entity.Bs_person;
import com.zmj.wkt.mapper.Bs_personMapper;
import com.zmj.wkt.service.Bs_permissionService;
import com.zmj.wkt.service.Bs_personService;
import com.zmj.wkt.utils.*;
import com.zmj.wkt.utils.sysenum.ErrorCode;
import com.zmj.wkt.utils.sysenum.SysConstant;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.sf.json.JSONObject;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 验证用户名密码正确后，生成一个token，并将token返回给客户端
 * 该类继承自UsernamePasswordAuthenticationFilter，重写了其中的2个方法
 * attemptAuthentication ：接收并解析用户凭证。
 * successfulAuthentication ：用户成功登录后，这个方法会被调用，我们在这个方法里生成token。
 * @author zmj
 */
public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter implements InitializingBean {

    @Autowired
    Bs_personService bs_personService;

    private static AuthenticationManager authenticationManager;

    public JWTLoginFilter(AuthenticationManager authenticationManager) {
        JWTLoginFilter.authenticationManager = authenticationManager;
    }

    @Override
    public void afterPropertiesSet() {
        init();
    }

    // 接收并解析用户凭证
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws CommonException {
        try {
            init();
            res.setHeader("Content-type", "text/html;charset=UTF-8");
            res.setHeader("Access-Control-Allow-Origin",req.getHeader("Origin"));
            res.setHeader("Access-Control-Allow-Credentials", "true");
            String code = req.getParameter("code");
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            if(!ZmjUtil.isNullOrEmpty(code)) {
                return WXAuthentication(req,res,code);
            }else if(!ZmjUtil.isNullOrEmpty(username)&&!ZmjUtil.isNullOrEmpty(password)){
                return normalAuthentication(req,res,username,password);
            }else {
                req.setAttribute("myStatus",ErrorCode.NULL_ERROR.getCode());
                throw new CommonException(ErrorCode.NULL_ERROR,"code为空！或账户密码为空！");
            }

        } catch (CommonException ce){
            throw ce;
        }catch (Exception e) {
            e.printStackTrace();
            throw new CommonException("登录失败："+e.getMessage());
        }
    }

    // 用户成功登录后，这个方法会被调用，我们在这个方法里生成token
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        Date date=new Date(System.currentTimeMillis() + 60 * 60 * 24 * 1000 *15);
        System.out.println("有效期到："+DateUtil.formatDate(date));
        String token = Jwts.builder()
                .setSubject(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername())
                //设置有效时间（毫秒）
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, "MyJwtSecret")
                .compact();
        res.addHeader("Authorization", "Bearer " + token);
        res.setHeader("Content-type", "text/html;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin",req.getHeader("Origin"));
        res.setHeader("Access-Control-Allow-Credentials", "true");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Authorization","Bearer " + token);
        res.getWriter().print(RestfulResultUtils.success(jsonObject).toString());
        res.getWriter().flush();
    }



    /**
     * 获取微信小程序 session_key 和 openid
     *
     * @author zmj
     * @param code 调用微信登陆返回的Code
     * @return
     */
    public static JSONObject getSessionKeyOropenid(String code){
        String requestUrl ="https://api.weixin.qq.com/sns/jscode2session";  //请求地址 https://api.weixin.qq.com/sns/jscode2session
        Map<String,String> requestUrlParam = new HashMap<String,String>();
        requestUrlParam.put("appid", SysConstant.WX_APPID);  //开发者设置中的appId
        requestUrlParam.put("secret", SysConstant.WX_SECRET); //开发者设置中的appSecret
        requestUrlParam.put("js_code", code); //小程序调用wx.login返回的code
        requestUrlParam.put("grant_type", "authorization_code");    //默认参数
        //发送post请求读取调用微信 https://api.weixin.qq.com/sns/jscode2session 接口获取openid用户唯一标识
        JSONObject jsonObject = JSONObject.fromObject(ZmjUtil.sendPost(requestUrl, requestUrlParam));
        return jsonObject;
    }

    /**
     * 微信登录
     * @param req
     * @param res
     * @return
     * @throws CommonException
     */
    public Authentication WXAuthentication(HttpServletRequest req,
                                                HttpServletResponse res,String code) throws CommonException {
        JSONObject sessionKeyOropenid = getSessionKeyOropenid(code);
        String openid = (String) sessionKeyOropenid.get("openid");
        if (ZmjUtil.isNullOrEmpty(openid)){
            req.setAttribute("myStatus",ErrorCode.NULL_ERROR.getCode());
            throw new CommonException(ErrorCode.NULL_ERROR, "根据code找不到对应的openid！");
        }
        System.out.println("openid:"+openid);

        Bs_person bs_person = bs_personService.findByWXOpenID(openid.toString());
        if (ZmjUtil.isNullOrEmpty(bs_person)){
            req.setAttribute("myStatus",ErrorCode.NOT_FIND_USER_ERROR2.getCode());
            throw new CommonException(ErrorCode.NOT_FIND_USER_ERROR2,"未找到该用户！");
        }
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        bs_person.getUserName(),
                        bs_person.getPersonPassword(),
                        new ArrayList<>())
        );
    }

    /**
     * 普通登录
     * @param req
     * @param res
     * @return
     * @throws CommonException
     */
    public Authentication normalAuthentication(HttpServletRequest req,
                                           HttpServletResponse res,String username , String password) throws CommonException {
        Bs_person bs_person = bs_personService.findByName(username);
        if (ZmjUtil.isNullOrEmpty(bs_person)){
            req.setAttribute("myStatus",ErrorCode.NOT_FIND_USER_ERROR2.getCode());
            throw new CommonException(ErrorCode.NOT_FIND_USER_ERROR2,"未找到该用户！");
        }
        if(!bs_person.getPersonPassword().equals(MD5Util.encode(password))){
            req.setAttribute("myStatus",ErrorCode.VERIFY_ERROR.getCode());
            throw new CommonException(ErrorCode.VERIFY_ERROR,"密码错误！");
        }
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        bs_person.getUserName(),
                        bs_person.getPersonPassword(),
                        new ArrayList<>())
        );
    }

    /**
     * 默认登录
     * @return
     * @throws CommonException
     */
    public static Authentication defaultAuthentication(Bs_person bs_person) throws CommonException {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        bs_person.getUserName(),
                        bs_person.getPersonPassword(),
                        new ArrayList<>())
        );
    }


    private void init(){
        if(bs_personService==null){
            bs_personService = (Bs_personService) SpringApplicationContextHolder.getSpringBean("bs_personServiceImpl");
        }
    }
}
