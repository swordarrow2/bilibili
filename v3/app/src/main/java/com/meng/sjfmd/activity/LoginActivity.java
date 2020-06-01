package com.meng.sjfmd.activity;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.widget.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.sjfmd.activity.*;
import com.meng.sjfmd.javabean.*;
import com.meng.sjfmd.libs.*;
import com.meng.sjfmd.result.*;

public class LoginActivity extends Activity implements View.OnClickListener {

    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;
	private Button login_button;
	private ProgressBar progressBar;
	private AccountInfo aci = new AccountInfo();
	private LoginResult loginResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = (ProgressBar) findViewById(R.id.activity_loginProgressBar);
		mUserNameView = (AutoCompleteTextView) findViewById(R.id.tv_user_name);
		mPasswordView = (EditText) findViewById(R.id.tv_password);
        login_button = (Button) findViewById(R.id.btn_login);
        login_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
		final String userName = mUserNameView.getText().toString();
		final String password = mPasswordView.getText().toString();
		progressBar.setVisibility(View.VISIBLE);
		try {
			aci.phone = Long.parseLong(userName);
		} catch (NumberFormatException e) {
			Snackbar.make(progressBar, "请输入正确的手机号", 3000).show();
			return;
		}
		aci.password = password;
		MainActivity.instance.threadPool.execute(new Runnable(){

				@Override
				public void run() {
					loginResult = UserLoginApi.login(userName, password);
					if (loginResult == null) {
						LoginActivity.this.runOnUiThread(new Runnable(){

								@Override
								public void run() {
									Snackbar.make(progressBar, "登录出现了一个错误", 3000).show();
									progressBar.setVisibility(View.GONE);
								}
							});
						return;
					}
					switch (loginResult.code) {
						case 0:
							String cookie = UserLoginApi.getCookie(loginResult.data.access_token);
							MyInfo myInfo = Tools.BilibiliTool.getMyInfo(cookie);
							for (AccountInfo ai:MainActivity.instance.loginAccounts) {
								if (ai.uid == myInfo.data.mid) {
									LoginActivity.this.runOnUiThread(new Runnable(){

											@Override
											public void run() {
												MainActivity.instance.showToast("已添加过此帐号");
												setResult(RESULT_CANCELED, null);
												finish();
											}
										});
									return;
								}
							}
							aci.cookie = cookie;
							aci.name = myInfo.data.name;
							aci.uid = myInfo.data.mid;
							LoginActivity.this.runOnUiThread(new Runnable(){

									@Override
									public void run() {
										progressBar.setVisibility(View.GONE);
										Intent in = new Intent();
										in.putExtra("aci", aci.toString());
										setResult(RESULT_OK, in);
										finish();
									}
								});
							break;
						case -629:
							LoginActivity.this.runOnUiThread(new Runnable(){

									@Override
									public void run() {
										Snackbar.make(progressBar, "账号或密码错误", 3000).show();
										progressBar.setVisibility(View.GONE);
									}
								});
							break;
						case -105:
							LoginActivity.this.runOnUiThread(new Runnable(){

									@Override
									public void run() {
										Snackbar.make(progressBar, "重试次数达到上线，请使用扫码登录或稍后再试", 3000).show();
										progressBar.setVisibility(View.GONE);
									}
								});
							break;
						case -2100:
							LoginActivity.this.runOnUiThread(new Runnable(){

									@Override
									public void run() {
										progressBar.setVisibility(View.GONE);
										Snackbar.make(progressBar, "账号存在风险,需要验证", 3000).setAction("从网页登录", new OnClickListener(){

												@Override
												public void onClick(View p1) {
													startActivity(new Intent(LoginActivity.this, Login.class));
													finish();
												}
											}).show();
									}
								});
							break;
						default:
							LoginActivity.this.runOnUiThread(new Runnable(){

									@Override
									public void run() {
										Snackbar.make(progressBar, loginResult.message == null ?"未知错误": GSON.toJson(loginResult), 3000).setAction("从网页登录", new OnClickListener(){

												@Override
												public void onClick(View p1) {
													startActivity(new Intent(LoginActivity.this, Login.class));
													finish();
												}
											}).show();
									}
								});
					}

				}
			});
		InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
		if (inputMethodManager != null) {
			inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}
}

