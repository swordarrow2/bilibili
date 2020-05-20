package com.meng.mbili.tools.MaterialDesign;

import android.content.*;
import android.content.res.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v4.content.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.meng.mbili.*;

public class MDEditText extends LinearLayout {

    private Context mContext;

    private EditText editText;
    private TextInputLayout mInputLayout;

	private String defaultValue=null;
    public MDEditText(Context cxt, AttributeSet attrs) {
        super(cxt, attrs);
        mContext = cxt;
        init();
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.MDEdittext);
        if (a != null) {
            editText.setTextColor(a.getColor(R.styleable.MDEdittext_textColor,  ContextCompat.getColor(mContext, R.color.color_c4)));
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimension(R.styleable.MDEdittext_textSize, mContext.getResources().getDimension(R.dimen.common_s3)));
			defaultValue = a.getString(R.styleable.MDEdittext_default_value);
            mInputLayout.setHint(a.getString(R.styleable.MDEdittext_hint) + " 默认:" + defaultValue);
            mInputLayout.setErrorEnabled(a.getBoolean(R.styleable.MDEdittext_errorEnable, false));
            if (mInputLayout.isErrorEnabled()) {
                mInputLayout.setError("");
			}
            a.recycle();
		}
	}

	public String getString() {
        return isEmpty() ? defaultValue: editText.getText().toString();
    }

    public int getInt() {
        return Integer.parseInt(getString());
    }

    public void setString(String s) {
        editText.setText(s);
    }

    private boolean isEmpty() {
        return editText.getText().toString().trim().length() == 0;
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.conmon_edittext_layout, this, true);
        editText = (EditText) findViewById(R.id.edittext);
        mInputLayout = (TextInputLayout) findViewById(R.id.input_layout);
	}

    public void setHintAnimationEnabled(boolean enabled) {
        mInputLayout.setHintAnimationEnabled(enabled);
	}

    public void setErrorEnabled(boolean enabled) {
        mInputLayout.setErrorEnabled(enabled);
	}

    public void setError(CharSequence error) {
        mInputLayout.setError(error);
	}

    public void setHintTextAppearance(int resid) {
        mInputLayout.setHintTextAppearance(resid);
	}

    public void setText(CharSequence text) {
        editText.setText(text);
	}

	public void addTextChangedListener(TextWatcher watcher) {
        editText.addTextChangedListener(watcher);
	}

    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        editText.setOnFocusChangeListener(l);
	}
	
	@Override
    public void setEnabled(boolean enabled){
        super.setEnabled(enabled);
        setVisibility(enabled?VISIBLE:GONE);
    }
}
