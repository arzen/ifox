package com.arzen.ifox;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.arzen.ifox.iFox.ChargeListener;
import com.arzen.ifox.iFox.InitCallBack;
import com.arzen.ifox.iFox.LoginListener;
import com.arzen.ifox.iFox.OnCommitScoreCallBack;
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
	
	private String key  = "313121500165700120";
	private String appSecrect = "db0938b04d97466b8a5f7ececff681a9052ac8479";

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

		iFox.init(this, key, appSecrect,new InitCallBack() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				MsgUtil.msg("初始化成功", MainActivity.this);
			}
			
			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				MsgUtil.msg("初始化失败", MainActivity.this);
			}
		});
	
		findViewById(R.id.btnCommit).setOnClickListener(mOnClickListener);
	}
	
	public OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.btnPay:
				Bundle bundle = new Bundle();
				bundle.putString(KeyConstants.INTENT_DATA_KEY_EXTRA, "sn=0668!!!&role=梁叉叉");
				bundle.putInt(KeyConstants.INTENT_DATA_KEY_PID, 333);
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
			case R.id.btnTop:
				iFox.leaderboardPage(MainActivity.this);
				break;
				
			case R.id.btnCommit:
				iFox.submitScore(MainActivity.this, 33939,0, new OnCommitScoreCallBack() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						MsgUtil.msg("提交分数成功!", MainActivity.this);
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						MsgUtil.msg("提交分数失败:" + msg, MainActivity.this);
					}
				});
				break;
			}
		}
	};
}
