package com.zmj.wkt.utils;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.NTbkItem;
import com.taobao.api.request.*;
import com.taobao.api.response.*;

import java.util.List;

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
public class TbkUtil {

    //正式环境
    private static final String URL = "https://eco.taobao.com/router/rest";
    //测试环境
    //private static final String URL = "https://gw.api.tbsandbox.com/router/rest";
    private static final String APPKEY = "24576611";
    private static final String SECRET = "20171995c8a67b8fecc47058c616704b";
    private static final String SESSION_KEY = "6102028455a615ca6077c4f1245bfec1c0e113df1737d011091412511";
    private static final String PID = "mm_46667186_35066962_277674842";

    /**
     * 生成淘口令
     * @param text
     * @param url
     * @param logo
     * @param ext
     * @return
     */
    public static String tpwdCreate(String PID,String text,String url,String logo,String ext){
        TaobaoClient client = new DefaultTaobaoClient(URL, APPKEY, SECRET);
        TbkTpwdCreateRequest req = new TbkTpwdCreateRequest();
        req.setUserId(String.valueOf(getAdzoneID(PID)));
        req.setText(text);
        req.setUrl(url);
        req.setLogo(logo);
        req.setExt(ext);
        TbkTpwdCreateResponse rsp = null;
        try {
            rsp = client.execute(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
        return  rsp.getBody();
    }

    /**
     * 获取商品列表
     * @param PID
     * @param Q
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static List<TbkDgItemCouponGetResponse.TbkCoupon> getGoodsList(String PID, String Q, Long pageNo , Long pageSize){
        TaobaoClient client = new DefaultTaobaoClient(URL, APPKEY, SECRET);
        TbkDgItemCouponGetRequest req = new TbkDgItemCouponGetRequest();
        req.setAdzoneId(getAdzoneID(PID));
        //手机端
        req.setPlatform(2L);
        req.setPageSize(pageSize);
        req.setQ(Q);
        req.setPageNo(pageNo);
        TbkDgItemCouponGetResponse rsp = null;
        try {
            rsp = client.execute(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
        return rsp.getResults();
    }

    /**
     * 获取商品列表
     * @param pageSize
     * @param pageNo
     * @return
     */
    public static String getGoodsList2(String Itemloc , String Q ,Long pageNo ,Long pageSize) throws ApiException {
        TaobaoClient client = new DefaultTaobaoClient(URL, APPKEY, SECRET);
        TbkItemGetRequest req = new TbkItemGetRequest();
        req.setFields("num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url,seller_id,volume,nick,commission_rate");
        req.setQ(Q);
        //req.setCat("16,18");
        req.setItemloc(Itemloc);
        req.setIsTmall(false);
        req.setIsOverseas(false);
        //手机端
        req.setPlatform(2L);
        req.setPageNo(pageNo);
        req.setPageSize(pageSize);
        req.setEndTkRate(20L);
        TbkItemGetResponse rsp = client.execute(req);
        System.out.println(rsp.getBody());
        return rsp.getBody();
    }

    public static String tpwdCreate2(Long userId,String text,String url,String logo,String ext) throws ApiException {
        TaobaoClient client = new DefaultTaobaoClient(URL, APPKEY, SECRET);
        WirelessShareTpwdCreateRequest req = new WirelessShareTpwdCreateRequest();
        WirelessShareTpwdCreateRequest.GenPwdIsvParamDto obj1 = new WirelessShareTpwdCreateRequest.GenPwdIsvParamDto();
        obj1.setExt(ext);
        obj1.setLogo(logo);
        obj1.setUrl(url);
        obj1.setText(text);
        obj1.setUserId(userId);
        req.setTpwdParam(obj1);
        WirelessShareTpwdCreateResponse rsp = client.execute(req);
        System.out.println(rsp.getBody());
        return rsp.getBody();
    }

    /**
     * 获取商品信息
     * @param numIids
     * @return
     * @throws ApiException
     */
    public static List<NTbkItem> getGoodInfo(String numIids) throws ApiException {
        TaobaoClient client = new DefaultTaobaoClient(URL, APPKEY, SECRET);
        TbkItemInfoGetRequest req = new TbkItemInfoGetRequest();
        req.setFields("num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url,seller_id,volume,nick,commission_rate");
        req.setPlatform(2L);
        req.setNumIids(numIids);
        TbkItemInfoGetResponse rsp = client.execute(req);
        System.out.println(rsp.getBody());
        return rsp.getResults();
    }


    /**
     * 超级搜索
     * @param PID
     * @param Q
     * @param pageNo
     * @param pageSize
     * @return
     * @throws ApiException
     */
    public static List<TbkScMaterialOptionalResponse.MapData> superSearchGoods(String PID , String Q , Long pageNo , Long pageSize) throws ApiException {
        TaobaoClient client = new DefaultTaobaoClient(URL, APPKEY, SECRET);
        TbkScMaterialOptionalRequest req = new TbkScMaterialOptionalRequest();
        req.setStartDsr(10L);
        req.setPageSize(pageSize);
        req.setPageNo(pageNo);
        //手机端
        req.setPlatform(2L);
        req.setStartTkRate(10L);
        //req.setSort("tk_rate_des");
        req.setQ(Q);
        req.setAdzoneId(getAdzoneID(PID));
        req.setSiteId(getSiteID(PID));
        TbkScMaterialOptionalResponse rsp = client.execute(req,SESSION_KEY);
        System.out.println(rsp.getBody());
        return rsp.getResultList();
    }

    public static Long getAdzoneID(String PID){
        Long adzoneId = Long.valueOf(PID.split("_")[3]);
        return adzoneId;
    }

    public static Long getSiteID(String PID){
        Long adzoneId = Long.valueOf(PID.split("_")[2]);
        return adzoneId;
    }

    public static void main(String[] args) throws ApiException {

        //测试商品列表获取
        Long adzoneId = getAdzoneID(PID);
        System.out.println(adzoneId);
        getGoodsList(PID,"女装",1L,10L);
        //getGoodsList2("杭州","女装",1L,10L);

        //url 转换

        //测试生成淘口令
        tpwdCreate(PID,"长度大于5个字符","https://detail.tmall.com/item.htm?id=537168972219","http://img.alicdn.com/tfscom/i1/2260407110/TB12KHwckfb_uJjSsD4XXaqiFXa_!!0-item_pic.jpg","{\"xx\":\"xx\"}");

        TaobaoClient client = new DefaultTaobaoClient(URL, APPKEY, SECRET);
        TbkItemGetRequest req = new TbkItemGetRequest();
        req.setFields("num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url,seller_id,volume,nick,tk_rate,commission_rate");
        req.setQ("女装");
        req.setCat("16,18");
        req.setItemloc("杭州");
        req.setSort("tk_rate_des");
        req.setIsTmall(false);
        req.setIsOverseas(false);
        req.setStartPrice(1L);
        req.setEndPrice(1000L);
        req.setStartTkRate(1000L);
        req.setEndTkRate(10L);
        req.setPageNo(1L);
        req.setPageSize(20L);
        //手机端
        req.setPlatform(2L);
        TbkItemGetResponse rsp = client.execute(req);
        System.out.println(rsp.getBody());

        getGoodInfo("536520714427");

        tpwdCreate2(131267237L,"长度大于5个字符","https://item.taobao.com/item.htm?id=564527851725","https://uland.taobao.com/","{\"xx\":\"xx\"}");

        superSearchGoods(PID,"女装",1L,10L);
    }
}
