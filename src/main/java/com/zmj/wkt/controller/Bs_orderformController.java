package com.zmj.wkt.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zmj.wkt.common.CommonController;
import com.zmj.wkt.common.RestfulResult;
import com.zmj.wkt.common.exception.CommonException;
import com.zmj.wkt.entity.Acc_daybook;
import com.zmj.wkt.entity.Bs_goods;
import com.zmj.wkt.entity.Bs_orderform;
import com.zmj.wkt.entity.Bs_person;
import com.zmj.wkt.mapper.Acc_daybookMapper;
import com.zmj.wkt.mapper.Acc_personMapper;
import com.zmj.wkt.mapper.Bs_orderformMapper;
import com.zmj.wkt.service.Acc_daybookService;
import com.zmj.wkt.service.Acc_personService;
import com.zmj.wkt.service.Bs_goodsService;
import com.zmj.wkt.service.Bs_orderformService;
import com.zmj.wkt.utils.DateUtil;
import com.zmj.wkt.utils.RestfulResultUtils;
import com.zmj.wkt.utils.ZmjUtil;
import com.zmj.wkt.utils.sysenum.ErrorCode;
import com.zmj.wkt.utils.sysenum.SysCode;
import com.zmj.wkt.utils.sysenum.TrCode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
@RestController
public class Bs_orderformController  extends CommonController{

    @Autowired
    Bs_orderformService bs_orderformService;

    @Autowired
    Acc_personService acc_personService;

    @Autowired
    Acc_daybookMapper acc_daybookMapper;

    @Autowired
    Acc_daybookService acc_daybookService;

    @Autowired
    Bs_goodsService bs_goodsService;



    @PostMapping("/applyOrderForm")
    public RestfulResult applyOrderForm(Bs_orderform bs_orderform, @RequestParam("imgFile") MultipartFile imgFile) throws Exception {
            Bs_person bs_person = this.getThisUser();
            if (imgFile.isEmpty()) {
                throw new CommonException(ErrorCode.NULL_ERROR,"上传图片文件为空！");
            }
            bs_orderform.setClientID(bs_person.getClientID());
            bs_orderform.setConsumerUserName(bs_person.getUserName());
            bs_orderform.setSubID("Sub_"+ UUID.randomUUID().toString().toUpperCase());
            bs_orderform.setState(SysCode.IS_ABLE_WAIT.getCode());
            bs_orderform.setIsAble(SysCode.IS_ABLE_WAIT.getCode());
            bs_orderform.setSpPrice(bs_orderform.getGPrice().multiply(BigDecimal.valueOf(bs_orderform.getSpCount())));
            //申请时间
            bs_orderform.setSpDate(DateUtil.getNowTimestamp());
        Bs_goods bs_goods = bs_goodsService.selectOne(new EntityWrapper<Bs_goods>().where("GoodsID = {0}", bs_orderform.getGoodsID()));
        if(bs_goods.getGCount()+bs_orderform.getSpCount()>bs_goods.getGMaxCount()){
            throw new CommonException("超出最大接单数！");
        }
        bs_orderformService.orderFormApply(bs_orderform,imgFile);
            return RestfulResultUtils.success("上传成功！等待审核...");
    }

    /**
     * 获取当前用户的待发送订单
     * @return
     * @throws Exception
     */
    @GetMapping("/findMyOrderForm")
    public  RestfulResult findMyOrderForm() throws Exception {
        Bs_person bs_person = getThisUser();
        return  RestfulResultUtils.success(bs_orderformService.findUserOrderFormList(bs_person.getClientID(),SysCode.STATE_TO_BE_SENT));
    }

    /**
     * 获取当前用户的发送成功订单
     * @return
     * @throws Exception
     */
    @GetMapping("/findMySuccessOrderForm")
    public  RestfulResult findMySuccessOrderForm() throws Exception {
        Bs_person bs_person = getThisUser();
        return  RestfulResultUtils.success(bs_orderformService.findUserOrderFormList(bs_person.getClientID(),SysCode.STATE_TO_SUCCESS));
    }

    /**
     * 获取当前用户的发送失败订单
     * @return
     * @throws Exception
     */
    @GetMapping("/findMyFailureOrderForm")
    public  RestfulResult findMyFailureOrderForm() throws Exception {
        Bs_person bs_person = getThisUser();
        return  RestfulResultUtils.success(bs_orderformService.findUserOrderFormList(bs_person.getClientID(),SysCode.STATE_TO_FAILURE));
    }

    /**
     * 获取当前接单用户待发送的订单
     * @return
     * @throws Exception
     */
    @GetMapping("/jd/getWaitOrderForm")
    public  RestfulResult getJdWaitOrderForm() throws Exception {
        Bs_person bs_person = getThisUser();
        return  RestfulResultUtils.success(bs_orderformService.findJdOrderFormList(bs_person.getUserName(),SysCode.STATE_TO_BE_SENT));
    }

    /**
     * 获取当前接单用户发送成功的订单
     * @return
     * @throws Exception
     */
    @GetMapping("/jd/getJdSuccessOrderForm")
    public  RestfulResult getJdSuccessOrderForm() throws Exception {
        Bs_person bs_person = getThisUser();
        return  RestfulResultUtils.success(bs_orderformService.findJdOrderFormList(bs_person.getUserName(),SysCode.STATE_TO_SUCCESS));
    }

    /**
     * 获取当前接单用户发送失败的订单
     * @return
     * @throws Exception
     */
    @GetMapping("/jd/getJdFailureOrderForm")
    public  RestfulResult getJdFailureOrderForm() throws Exception {
        Bs_person bs_person = getThisUser();
        return  RestfulResultUtils.success(bs_orderformService.findJdOrderFormList(bs_person.getUserName(),SysCode.STATE_TO_FAILURE));
    }

    /**
     * 订单确认发送成功
     * @param SubID
     * @return
     * @throws Exception
     */
    @PostMapping("/sendSuccess")
    public RestfulResult sendSuccess(String SubID) throws Exception {
        Bs_person bs_person = getThisUser();
        EntityWrapper bs_orderformWrapper = new EntityWrapper();
        bs_orderformWrapper.setEntity(new Bs_orderform());
        bs_orderformWrapper.where("ProductUserName = {0} and State = {1} and SubID = {2}",
                bs_person.getUserName(), SysCode.STATE_TO_BE_SENT.getCode(),SubID);
        Bs_orderform bs_orderform = bs_orderformService.selectOne(bs_orderformWrapper);
        if(ZmjUtil.isNullOrEmpty(bs_orderform)){
            throw new CommonException(ErrorCode.NOT_FIND_ERROR,"找不到该订单！");
        }
        EntityWrapper acc_daybookWrapper = new EntityWrapper();
        acc_daybookWrapper.setEntity(new Acc_daybook());
        acc_daybookWrapper.where("tr_code = {0} and State = {1} and SubID = {2}",TrCode.WITHHOLDING.getCode(),SysCode.STATE_T.getCode(),bs_orderform.getSubID());
        Acc_daybook acc_daybook = acc_daybookService.selectOne(acc_daybookWrapper);
        if(ZmjUtil.isNullOrEmpty(acc_daybook)){
            throw new CommonException(ErrorCode.NOT_FIND_ERROR,"找不到该订单的代扣记录！");
        }
        bs_orderformService.orderSuccess(bs_orderform,bs_person,acc_daybook);
        return  RestfulResultUtils.success("确认成功！");
    }

    /**
     * 订单确认支付发送成功
     * @param SubID
     * @return
     * @throws Exception
     */
    @PostMapping("/orderPaySuccess")
    public RestfulResult orderPaySuccess(String SubID) throws Exception {
        Bs_person bs_person = getThisUser();
        EntityWrapper bs_orderformWrapper = new EntityWrapper();
        bs_orderformWrapper.setEntity(new Bs_orderform());
        bs_orderformWrapper.where("ClientID = {0} and State = {1} and SubID = {2}",
                bs_person.getClientID(), SysCode.STATE_TO_BE_SENT.getCode(),SubID);
        Bs_orderform bs_orderform = bs_orderformService.selectOne(bs_orderformWrapper);
        if(ZmjUtil.isNullOrEmpty(bs_orderform)){
            throw new CommonException(ErrorCode.NOT_FIND_ERROR,"找不到该订单！");
        }
        if(SysCode.STATE_T.getCode()!=bs_orderform.getMerchantConfirmation()){
            throw new CommonException("商户尚未确认该订单！");
        }
        EntityWrapper acc_daybookWrapper = new EntityWrapper();
        acc_daybookWrapper.setEntity(new Acc_daybook());
        acc_daybookWrapper.where("tr_code = {0} and State = {1} and SubID = {2}",TrCode.WITHHOLDING.getCode(),SysCode.STATE_T.getCode(),bs_orderform.getSubID());
        Acc_daybook acc_daybook = acc_daybookService.selectOne(acc_daybookWrapper);
        if(ZmjUtil.isNullOrEmpty(acc_daybook)){
            throw new CommonException(ErrorCode.NOT_FIND_ERROR,"找不到该订单的代扣记录！");
        }
        bs_orderformService.orderPaySuccess(bs_orderform,bs_person,acc_daybook);
        return  RestfulResultUtils.success("确认成功！");
    }
}
