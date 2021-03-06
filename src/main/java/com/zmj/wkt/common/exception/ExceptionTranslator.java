package com.zmj.wkt.common.exception;

import com.zmj.wkt.common.RestfulResult;
import com.zmj.wkt.entity.Sys_error_log;
import com.zmj.wkt.mapper.Sys_error_logMapper;
import com.zmj.wkt.utils.DateUtil;
import com.zmj.wkt.utils.RestfulResultUtils;
import com.zmj.wkt.utils.ZmjUtil;
import com.zmj.wkt.utils.sysenum.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常处理
 * @author zmj
 */
@ControllerAdvice
public class ExceptionTranslator {
    public static final String DEFAULT_ERROR_VIEW = "exception_error";
    private static final Logger logger  = LoggerFactory.getLogger(ExceptionTranslator.class);

    @Autowired
    Sys_error_logMapper sys_error_logMapper;

    /**
     * 未知异常
     * @param req
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName(DEFAULT_ERROR_VIEW);
        Sys_error_log sys_error_log = new Sys_error_log();
        sys_error_log.setErrorCode(String.valueOf(ErrorCode.UNKNOWNS_ERROR.getCode()));
        sys_error_log.setMessage(e.getMessage());
        sys_error_log.setDatetime(DateUtil.getNowTimestamp());
        sys_error_logMapper.insert(sys_error_log);
        return mav;
    }

    /**
     * 自定义异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = CommonException.class)
    @ResponseBody
    public RestfulResult handle(CommonException e){
        Sys_error_log sys_error_log = new Sys_error_log();
        if(ZmjUtil.isNullOrEmpty(e.getCode())){
            logger.warn(RestfulResultUtils.error(ErrorCode.UNKNOWNS_ERROR.getCode(), e.getMessage().trim()).toString());
            //插入数据库
            sys_error_log.setErrorCode(String.valueOf(ErrorCode.UNKNOWNS_ERROR.getCode()));
            sys_error_log.setMessage(e.getMessage());
            sys_error_log.setDatetime(DateUtil.getNowTimestamp());
            sys_error_logMapper.insert(sys_error_log);
            return RestfulResultUtils.error(ErrorCode.UNKNOWNS_ERROR.getCode(),e.getMessage().trim());
        }
        else{
            logger.warn(RestfulResultUtils.error(e.getCode(), e.getMessage().trim()).toString());
            //插入数据库
            sys_error_log.setErrorCode(String.valueOf(e.getCode()));
            sys_error_log.setMessage(e.getMessage());
            sys_error_log.setDatetime(DateUtil.getNowTimestamp());
            sys_error_logMapper.insert(sys_error_log);
            return RestfulResultUtils.error(e.getCode(),e.getMessage().trim());
        }
    }

    /**
     * httpMethod异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public RestfulResult handle(HttpRequestMethodNotSupportedException e){
        logger.warn(RestfulResultUtils.error(ErrorCode.HTTPREQUESTMETHODNOTSUPPORTED.getCode(), ErrorCode.HTTPREQUESTMETHODNOTSUPPORTED.getDescription() + ":" + e.getMessage().trim()).toString());
        //插入数据库
        Sys_error_log sys_error_log = new Sys_error_log();
        sys_error_log.setErrorCode(String.valueOf(ErrorCode.HTTPREQUESTMETHODNOTSUPPORTED.getCode()));
        sys_error_log.setMessage(e.getMessage());
        sys_error_log.setDatetime(DateUtil.getNowTimestamp());
        sys_error_logMapper.insert(sys_error_log);
        return RestfulResultUtils.error(ErrorCode.HTTPREQUESTMETHODNOTSUPPORTED.getCode(),ErrorCode.HTTPREQUESTMETHODNOTSUPPORTED.getDescription()+":"+e.getMessage().trim());
    }

    /**
     * token异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = io.jsonwebtoken.ExpiredJwtException.class)
    @ResponseBody
    public RestfulResult handle(io.jsonwebtoken.ExpiredJwtException e){
        logger.warn(RestfulResultUtils.error(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getDescription() + ":" + e.getMessage().trim()).toString());
        //插入数据库
        Sys_error_log sys_error_log = new Sys_error_log();
        sys_error_log.setErrorCode(String.valueOf(ErrorCode.TOKEN_ERROR.getCode()));
        sys_error_log.setMessage(e.getMessage());
        sys_error_log.setDatetime(DateUtil.getNowTimestamp());
        sys_error_logMapper.insert(sys_error_log);
        return RestfulResultUtils.error(ErrorCode.TOKEN_ERROR.getCode(),e.getMessage().trim());
    }

    /**
     * 断言校验异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseBody
    public RestfulResult handle(IllegalArgumentException e){
        logger.warn(RestfulResultUtils.error(ErrorCode.VERIFY_ERROR.getCode(), ErrorCode.VERIFY_ERROR.getDescription() + ":" + e.getMessage().trim()).toString());
        //插入数据库
        Sys_error_log sys_error_log = new Sys_error_log();
        sys_error_log.setErrorCode(String.valueOf(ErrorCode.VERIFY_ERROR.getCode()));
        sys_error_log.setMessage(e.getMessage());
        sys_error_log.setDatetime(DateUtil.getNowTimestamp());
        sys_error_logMapper.insert(sys_error_log);
        return RestfulResultUtils.error(ErrorCode.VERIFY_ERROR.getCode(),e.getMessage().trim());
    }
}
