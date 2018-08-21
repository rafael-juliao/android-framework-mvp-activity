package com.lfyt.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;

import com.lfyt.mobile.android.frameworkmvp.archtecture.L;
import com.lfyt.mobile.android.livemodel.Event;
import com.lfyt.mobile.android.livemodel.LiveModel;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class control the onServiceStarted of new activities
 * and the activityResult in case of startActivityForResults
 *
 * ## EVENTS
 *
 * BackPressedEvent => on the user press back [MUST BE HANDLE]
 *
 * ActivityResultEvent => Event when an activity returns an result
 */
@Singleton
public class ActivityAPI extends LiveModel {

	@Inject
	public ActivityAPI(){
		L.DI(this);
	}

	//##################################################
	//##################################################
	//###
	//###  Navigate/Return to/from Activity
	//###
	//##################################################
	//##################################################


	///////////////////////////////////////////////////////////////////////////
	// BACK BUTTON PRESSED
	///////////////////////////////////////////////////////////////////////////

	public class BackPressedEvent extends Event {}

	public void onBackPressed() {
		post(new BackPressedEvent());
	}



	///////////////////////////////////////////////////////////////////////////
	// GO BACK ACTIVITY
	///////////////////////////////////////////////////////////////////////////
	
	public void goBackActivity(Activity caller){
		caller.finish();
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////
	// Start Activity
	///////////////////////////////////////////////////////////////////////////
	
	public void startActivity(Activity caller, Class<?> activityToStart){
		L.I(caller, "Starting Activity -> %s", activityToStart.getSimpleName());
		caller.startActivity(new Intent(caller, activityToStart));
	}
	
	
	public void startActivityCloseAll(Activity caller, Class<?> activityToStart){
		L.I(caller, "Starting Activity (CLOSE ALL OTHERS) -> %s", activityToStart.getSimpleName());
		caller.finish();
		Intent intent = new Intent(caller, activityToStart);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		caller.startActivity(intent);
	}
	

	
	
	//##################################################
	//##################################################
	//###
	//###  Start Activity For Result
	//###
	//##################################################
	//##################################################
	
	//Hold response
	private Event activityResult;
	
	@Override
	public void post(Event event) {
		if( event instanceof ActivityResultEvent){
			activityResult = event;
			return;
		}
		super.post(event);
	}
	
	
	//Check pending response
	public void checkPendingActivityResult() {
		if( activityResult == null ) return;
		super.post(activityResult);
		activityResult = null;
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////
	// Request Activity Result Via Activity To Start
	///////////////////////////////////////////////////////////////////////////
	
	void requestActivityResult(Class<?> activityToStart, int code){
		post(new RequestActivityResultEvent(activityToStart, code));
	}
	
	public class RequestActivityResultEvent extends Event{
		Class<?> activityToStart;
		private final int code;
		
		public Class<?> getActivityToStart() {
			return activityToStart;
		}
		
		public RequestActivityResultEvent(Class<?> activityToStart, int code) {
			this.activityToStart = activityToStart;
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}
	}
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////
	// Request Activity Result Via Intent
	///////////////////////////////////////////////////////////////////////////
	
	void requestActivityResult(Intent intent, int code){
		post(new RequestActivityResultIntentEvent(intent, code));
	}
	
	public class RequestActivityResultIntentEvent extends Event{
		private final Intent intent;
		private final int code;
		
		public RequestActivityResultIntentEvent(Intent intent, int code) {
			
			this.intent = intent;
			this.code = code;
		}
		
		public Intent getIntent() {
			return intent;
		}
		
		public int getCode() {
			return code;
		}
	}
	
	
	
	
	///////////////////////////////////////////////////////////////////////////
	// Activity Result
	///////////////////////////////////////////////////////////////////////////
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		post(new ActivityResultEvent(requestCode, resultCode, data));
	}
	
	
	
	public class ActivityResultEvent extends Event {
		int requestCode;
		int resultCode;
		Intent data;
		
		public ActivityResultEvent(int requestCode, int resultCode, Intent data) {
			this.requestCode = requestCode;
			this.resultCode = resultCode;
			this.data = data;
		}
		
		public int getRequestCode() {
			return requestCode;
		}
		
		public int getResultCode() {
			return resultCode;
		}
		
		public Intent getData() {
			return data;
		}
	}
	
	
	
}
