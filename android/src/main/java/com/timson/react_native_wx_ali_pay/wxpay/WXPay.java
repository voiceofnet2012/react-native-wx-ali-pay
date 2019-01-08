package com.timson.react_native_wx_ali_pay.wxpay;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.timson.react_native_wx_ali_pay.RNWxAliPayListener;




/**
 * @author Timson
 */
public class WXPay implements IWXAPIEventHandler {

    public static WXPay mWxPay = new WXPay();

    private IWXAPI mWxApi ;
    private RNWxAliPayListener payListener;

    public static WXPay newInstance() {
        if(mWxPay==null){
            mWxPay = new WXPay();
        }
        return mWxPay;
    }

    /**
     * 微信注册
     */
    public WXPay registerWxApi(Activity activity, String appId){
        mWxApi = WXAPIFactory.createWXAPI(activity, null);
        mWxApi.registerApp(appId);
        return newInstance();
    }


    /**
     * 发起支付
     */
    public void pay(RNWxAliPayListener listener,BaseReq req){
        this.payListener = listener;

        if(mWxApi==null){
            listener.onPayFail("-1","清先注册微信");
            return;
        }

        if (!mWxApi.isWXAppInstalled()) {
            listener.onPayFail("-1","您手机尚未安装微信，请安装后再登录");
            return;
        }

        Log.i("WXPayResultRecevier","发起支付");
        mWxApi.sendReq(req);
    }


    public static void handlerIntent(Intent intent){
        mWxPay.mWxApi.handleIntent(intent,mWxPay);
    }


    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        int errCode = baseResp.errCode;
        switch (errCode){
            case 0:
                this.payListener.onPaySuccess("支付成功");
                break;
            case -1:
                this.payListener.onPayFail("-1","支付失败"); //签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
                break;
            case -2:
                this.payListener.onPayFail("-1","支付取消");
                break;
        }
    }
}
