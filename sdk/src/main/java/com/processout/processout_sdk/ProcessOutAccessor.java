package com.processout.processout_sdk;

import android.app.Application;

import com.processout.sdk.core.annotation.ProcessOutInternalApi;

@ProcessOutInternalApi
public final class ProcessOutAccessor {

    @ProcessOutInternalApi
    public static ProcessOut initLegacyProcessOut(Application application, String projectId) {
        return new ProcessOut(application, projectId);
    }
}
