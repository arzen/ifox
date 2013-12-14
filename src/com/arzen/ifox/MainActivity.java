package com.arzen.ifox;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.arzen.ifox.iFox.ChangePasswordListener;
import com.arzen.ifox.iFox.ChargeListener;
import com.arzen.ifox.iFox.LoginListener;
import com.arzen.ifox.setting.KeyConstants;
import com.arzen.ifox.utils.MsgUtil;
/**
 * 默认为游戏主界面测试
 * @author Encore.liang
 *
 */
public class MainActivity extends Activity {
	
	private Button mBtnPay;
	private Button mBtnLogin;
	private Button mBtnChangePassword;
	private Button mBtnTop;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mBtnPay = (Button) findViewById(R.id.btnPay);
		mBtnPay.setOnClickListener(mOnClickListener);
		
		mBtnLogin = (Button) findViewById(R.id.btnLogin);
		mBtnLogin.setOnClickListener(mOnClickListener);
		
		mBtnChangePassword = (Button) findViewById(R.id.btnChangePassword);
		mBtnChangePassword.setOnClickListener(mOnClickListener);
		
		mBtnTop = (Button) findViewById(R.id.btnTop);
		mBtnTop.setOnClickListener(mOnClickListener);

		iFox.init(this, null, null);
	
		
	}
	
	public OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.btnPay:
				Bundle bundle = new Bundle();
				bundle.putString(KeyConstants.INTENT_DATA_KEY_EXTRA, "sn=0668!!!&role=梁叉叉");
				bundle.putInt(KeyConstants.INTENT_DATA_KEY_PID, 3668);
				bundle.putFloat(KeyConstants.INTENT_DATA_KEY_AMOUNT, 200f); //单位分 
				iFox.chargePage(MainActivity.this, bundle, new ChargeListener() {
					
					@Override
					public void onSuccess(Bundle bundle) {
						// TODO Auto-generated method stub
						//商品id
						int pid = bundle.getInt(KeyConstants.INTENT_DATA_KEY_PID);
						String orderId = bundle.getString(KeyConstants.INTENT_DATA_KEY_ORDERID);//订单id
						float amount = bundle.getFloat(KeyConstants.INTENT_DATA_KEY_AMOUNT); //价钱
						
						MsgUtil.msg("支付成功 ! 商品id:"+ pid + " 订单id:" + orderId + " 价格:" + amount, MainActivity.this);
					}
					
					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						MsgUtil.msg("onFinish()", MainActivity.this);
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						MsgUtil.msg("支付失败:" + msg, MainActivity.this);
					}
					
					@Override
					public void onCancel() {
						// TODO Auto-generated method stub
						MsgUtil.msg("支付取消", MainActivity.this);
					}
				});
				break;
			case R.id.btnLogin:
				iFox.loginPage(MainActivity.this, null, new LoginListener() {
					
					@Override
					public void onSuccess(Bundle bundle) {
						// TODO Auto-generated method stub
						String token = bundle.getString(KeyConstants.INTENT_DATA_KEY_TOKEN);
						MsgUtil.msg("login onSuccess! token is " + token, MainActivity.this);
					}
					
					@Override
					public void onCancel() {
						// TODO Auto-generated method stub
						MsgUtil.msg("login onCancel()", MainActivity.this);
					}
				});
				break;
			case R.id.btnChangePassword:
				
				iFox.changePassword(MainActivity.this, null, new ChangePasswordListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						MsgUtil.msg("chanPassword onSuccess():" , MainActivity.this);
					}
					
					@Override
					public void onCancel() {
						// TODO Auto-generated method stub
						MsgUtil.msg("chanPassword onCancel():" , MainActivity.this);
					}
				});
				break;
			case R.id.btnTop:
				iFox.TopPage(MainActivity.this);
				break;
			}
		}
	};
}
