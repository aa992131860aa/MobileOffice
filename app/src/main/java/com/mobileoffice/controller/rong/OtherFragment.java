package com.mobileoffice.controller.rong;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileoffice.R;

/**
 * 不用关注此类 做ViewPager + Fragment 的配合类
 * @author AM
 *
 */
public class OtherFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.other_fragment, container, false);
	}
}
