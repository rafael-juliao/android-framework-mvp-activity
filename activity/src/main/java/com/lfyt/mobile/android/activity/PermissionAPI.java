package com.lfyt.mobile.android.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.lfyt.mobile.android.frameworkmvp.archtecture.L;
import com.lfyt.mobile.android.frameworkmvp.archtecture.application.ActivityLifecycleAPI;
import com.lfyt.mobile.android.livemodel.Event;
import com.lfyt.mobile.android.livemodel.LiveModel;

import java.util.HashMap;
import java.util.Map;

public class PermissionAPI extends LiveModel {
	
	
	private final Context context;
	private final SharedPreferences sharedPreferences;
	
	private final Map<String, Boolean> permissionNeverAsk;
	private final ActivityLifecycleAPI activityLifecycleAPI;

	public PermissionAPI(ActivityLifecycleAPI activityLifecycleAPI, Context context) {
        this.activityLifecycleAPI = activityLifecycleAPI;
        L.DI(this);
		this.context = context;
		
		sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		permissionNeverAsk = new HashMap<>();
		
		load();
		
	}
	
	
	//////////////////////////////////
	////////// PERMISSIONS ///////////
	//////////////////////////////////
	
	private void load(){
		
		for(PermissionType permissionType : PermissionType.values()){
			permissionNeverAsk.put(permissionType.name(), sharedPreferences.getBoolean(permissionType.name(), false));
		}
		
	}

	private void save(){
		SharedPreferences.Editor editor = sharedPreferences.edit();
		
		for(PermissionType permissionType : PermissionType.values()){
			editor.putBoolean(permissionType.name(), permissionNeverAsk.get(permissionType.name()));
		}
		
		editor.commit();
	}
	
	
	public enum PermissionType {
		
		
		//CALENDAR
		READ_CALENDAR(0, Manifest.permission.READ_CALENDAR),
		WRITE_CALENDAR(1, Manifest.permission.WRITE_CALENDAR),
		
		
		//CAMERA
		CAMERA(2, Manifest.permission.CAMERA),
		
		
		//CONTACTS
		READ_CONTACTS(3, Manifest.permission.READ_CONTACTS),
		WRITE_CONTACTS(4, Manifest.permission.WRITE_CONTACTS),
		GET_ACCOUNTS(5, Manifest.permission.GET_ACCOUNTS),
		
		
		//LOCATION
		ACCESS_FINE_LOCATION(6, Manifest.permission.ACCESS_FINE_LOCATION),
		ACCESS_COARSE_LOCATION(7, Manifest.permission.ACCESS_COARSE_LOCATION),
		
		
		//MIC
		RECORD_AUDIO(8, Manifest.permission.RECORD_AUDIO),
		
		
		//PHONE
		READ_PHONE_STATE(9, Manifest.permission.READ_PHONE_STATE),
		@TargetApi(26)READ_PHONE_NUMBERS(10, Manifest.permission.READ_PHONE_NUMBERS),
		CALL_PHONE(11, Manifest.permission.CALL_PHONE),
		READ_CALL_LOG(12, Manifest.permission.READ_CALL_LOG),
		WRITE_CALL_LOG(13, Manifest.permission.WRITE_CALL_LOG),
		ADD_VOICEMAIL(14, Manifest.permission.ADD_VOICEMAIL),
		USE_SIP(15, Manifest.permission.USE_SIP),
		PROCESS_OUTGOING_CALLS(16, Manifest.permission.PROCESS_OUTGOING_CALLS),
		@TargetApi(26)ANSWER_PHONE_CALLS(17, Manifest.permission.ANSWER_PHONE_CALLS),
		
		
		//SENSORS
		@TargetApi(20)BODY_SENSORS(18, Manifest.permission.BODY_SENSORS),
		
		
		//SMS
		SEND_SMS(19, Manifest.permission.SEND_SMS),
		RECEIVE_SMS(20, Manifest.permission.RECEIVE_SMS),
		READ_SMS(21, Manifest.permission.READ_SMS),
		RECEIVE_WAP_PUSH(22, Manifest.permission.RECEIVE_WAP_PUSH),
		RECEIVE_MMS(23, Manifest.permission.RECEIVE_MMS),
		
		
		//STORAGE
		READ_EXTERNAL_STORAGE(24, Manifest.permission.READ_EXTERNAL_STORAGE),
		WRITE_EXTERNAL_STORAGE(25, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		
		
		private final int requestCode;
		private final String permission;
		
		PermissionType(int requestCode, String permission) {
			this.requestCode = requestCode;
			this.permission = permission;
		}
		
		public int getRequestCode() {
			return requestCode;
		}
		
		public String getPermission() {
			return permission;
		}
	}


	///////////////////////////////////////////////////////////////////////////
	// PermissionAPI Interface
	///////////////////////////////////////////////////////////////////////////


	///////////////////////////////////////////////////////////////////////////
	// Check Permission State
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Check if is required to ask permission
	 * @return true if is required or false if it's not
	 */
	public boolean isRequiredToAskPermission(){
	  //TODO: Possible pass PermissionType as argument then see individual if its required
		L.D(this, "Is required to Ask Permission -> %s", Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
	}

	/**
	 * Check if the app has the permission
	 * @param permissionType the type of the permission
	 * @return true if has or false if not
	 */
	public boolean hasPermission(PermissionType permissionType) {
		return (ContextCompat.checkSelfPermission(context, permissionType.getPermission()) == PackageManager.PERMISSION_GRANTED);
	}


	/**
	 * If the app can ask for the permission or the user marked "Never Ask"
	 * @param permissionType the type of the permission
	 * @return true if can ask, or false if should never ask again
	 */
	public boolean canAskPermission(PermissionType permissionType){
		return !permissionNeverAsk.get(permissionType.name());
	}

	/**
	 * If the app should explain why need the permission,
	 * only true when the user rejected the permission once
	 * and now he can mark "Never Ask"
	 * @param permissionType  the type of the permission
	 * @return true if should explain or false if should not
	 */
	public boolean shouldExplainBeforeAsk(PermissionType permissionType, Activity activity){
		return ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionType.getPermission());
	}




	///////////////////////////////////////////////////////////////////////////
	// Request Permission
	///////////////////////////////////////////////////////////////////////////

    public void requestPermission(PermissionType permissionType) {
        post( new RequestPermissionEvent(permissionType) );
    }

    public class RequestPermissionEvent extends Event {
	
	    private final PermissionType permissionType;
	
	    public RequestPermissionEvent(PermissionType permissionType) {
        	this.permissionType = permissionType;
        }
	
	    public PermissionType getPermissionType() {
		    return permissionType;
	    }
    }




	///////////////////////////////////////////////////////////////////////////
	// Permission Response Handling
	///////////////////////////////////////////////////////////////////////////

    public void onRequestPermissionResult(int requestCode, int[] grantResults) {
	   
		for( PermissionType permissionType : PermissionType.values() ){
			
			if( permissionType.getRequestCode() == requestCode ){
				
				//Cancelled
				if( grantResults == null || grantResults.length == 0 ){
					L.I(this, "Permission %s ==> CANCELLED", permissionType.getPermission());
					post(new PermissionCancelled(permissionType));
				}
				
				//Accepted
				else if( grantResults[0] == PackageManager.PERMISSION_GRANTED ){
					L.I(this, "Permission %s ==> GRANTED", permissionType.getPermission());
					post(new PermissionAccepted(permissionType));
				}
				
				//Denied
				else{
					
					//If it was denied, and i should not explain, means NEVER ASK is TRUE
					if( !shouldExplainBeforeAsk(permissionType, activityLifecycleAPI.getCurrentActivity()) ){
						L.I(this, "Permission %s ==> DENIED + NEVER ASK", permissionType.getPermission());
						permissionNeverAsk.put(permissionType.name(), true);
						save();
						post(new PermissionDenied(permissionType));
					}

					//If i should explain, then it was denied
					else{
						L.I(this, "Permission %s ==> DENIED", permissionType.getPermission());
						post(new PermissionDenied(permissionType));
					}
				}
				
			}
			
	    }

    }
	
	
	///////////////////////////////////////////////////////////////////////////
	// Permission Response Events
	///////////////////////////////////////////////////////////////////////////
	
	public class PermissionAccepted extends Event {

		private final PermissionType permissionType;
		
		private PermissionAccepted(PermissionType permissionType) {
			this.permissionType = permissionType;
		}
		
		public PermissionType getPermissionType() {
			return permissionType;
		}
		
	}
	
	
	
	public class PermissionDenied extends Event {
		
		private final PermissionType permissionType;
		
		private PermissionDenied(PermissionType permissionType) {
			this.permissionType = permissionType;
		}
		
		public PermissionType getPermissionType() {
			return permissionType;
		}
		
	}
	
	
	public class PermissionCancelled extends Event {
		
		private final PermissionType permissionType;
		
		private PermissionCancelled(PermissionType permissionType) {
			this.permissionType = permissionType;
		}
		
		public PermissionType getPermissionType() {
			return permissionType;
		}
		
	}
	
	
}
