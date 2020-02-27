package com.meng.biliv3.fragment;
import android.app.*;
import android.os.*;
import android.view.*;
import com.meng.biliv3.*;

public class UidFragment extends Fragment {
	
	private int id;
	
	public UidFragment(int uid){
		id=uid;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.uid_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);


		
	}

}
