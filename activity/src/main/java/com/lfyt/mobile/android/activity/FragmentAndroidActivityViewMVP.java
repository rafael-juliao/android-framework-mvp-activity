package com.lfyt.mobile.android.activity;

import android.support.v4.app.Fragment;

import com.lfyt.mobile.android.frameworkmvp.archtecture.mvp.view.BaseView;

/**
 * Created by rafaeljuliao on 11/05/18.
 */

public abstract class FragmentAndroidActivityViewMVP extends AndroidActivityViewMVP implements BaseView {
	
	@Override
	public int getLayout() {
		return R.layout.fragment_activity;
	}
	
	@Override
	public void setupView() {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.fragment_container, setupFragment(), "FRAGMENT")
				.commit();
		
	}
	
	protected abstract Fragment setupFragment();
	
	protected final Fragment getFragment(){
		return getSupportFragmentManager().findFragmentByTag("FRAGMENT");
	}
}
