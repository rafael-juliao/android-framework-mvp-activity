package com.lfyt.mobile.android.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.lfyt.mobile.android.frameworkmvp.archtecture.mvp.view.ActivityViewMVP;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by rafael on 30/10/17.
 */

public abstract class AndroidActivityViewMVP extends ActivityViewMVP {

    @Inject
    PermissionAPI permissionAPI;
	
	@Inject
	ActivityAPI activityAPI;
	
	@Override
    public void onStart() {
        permissionAPI.subscribe(permissionCode);
		activityAPI.subscribe(activityResultCode);
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
	    activityAPI.checkPendingActivityResult();
    }

    @Override
    public void onStop() {
        super.onStop();
        permissionAPI.unsubscribe(permissionCode);
	    activityAPI.unsubscribe(activityResultCode);

    }


    ///////////////////////////////////////////////////////////////////////////
    // Permissions
    ///////////////////////////////////////////////////////////////////////////
    Object permissionCode = new Object(){
        @Subscribe
        public void onRequestPermissionEvent(PermissionAPI.RequestPermissionEvent event){
            ActivityCompat.requestPermissions(
                    AndroidActivityViewMVP.this,
                    new String[]{event.getPermissionType().getPermission()},
                    event.getPermissionType().getRequestCode()
            );
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionAPI.onRequestPermissionResult(requestCode, grantResults);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Activity Result
    ///////////////////////////////////////////////////////////////////////////

    Object activityResultCode = new Object(){
        @Subscribe
        public void onRequestActivityResultEvent(ActivityAPI.RequestActivityResultEvent event){
            startActivityForResultWrapper(event.getActivityToStart(), event.getCode());
        }

        @Subscribe
        public void onRequestActivityResultIntentEvent(ActivityAPI.RequestActivityResultIntentEvent event){
            startActivityForResultIntentWrapper(event.getIntent(), event.getCode());
        }

    };

    private void startActivityForResultWrapper(Class<?> activityToStart, int code) {
        startActivityForResult(new Intent(this, activityToStart), code);
    }

    private void startActivityForResultIntentWrapper(Intent intent, int code) {
        startActivityForResult(intent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    activityAPI.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
	
	
	///////////////////////////////////////////////////////////////////////////
	// On Back
	///////////////////////////////////////////////////////////////////////////
	
	
	@Override
	public void onBackPressed() {
		activityAPI.onBackPressed();
	}
}
