package com.lfyt.mobile.android.activity;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidActivityModule {

    @Provides
    @Singleton
    ActivityAPI activityAPI(Application application){
        return new ActivityAPI(application);
    }

    @Provides
    @Singleton
    PermissionAPI permissionAPI(ActivityAPI activityAPI, Context context){
        return new PermissionAPI(activityAPI, context);
    }
}
