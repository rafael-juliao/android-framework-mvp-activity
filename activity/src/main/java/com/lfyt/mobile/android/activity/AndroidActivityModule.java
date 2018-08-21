package com.lfyt.mobile.android.activity;

import android.app.Application;
import android.content.Context;

import com.lfyt.mobile.android.frameworkmvp.archtecture.application.ActivityLifecycleAPI;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidActivityModule {

    @Provides
    @Singleton
    ActivityAPI activityAPI(){
        return new ActivityAPI();
    }

    @Provides
    @Singleton
    PermissionAPI permissionAPI(ActivityLifecycleAPI activityLifecycleAPI, Context context){
        return new PermissionAPI(activityLifecycleAPI, context);
    }
}
