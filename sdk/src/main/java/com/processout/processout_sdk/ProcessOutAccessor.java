package com.processout.processout_sdk;

import android.app.Application;

import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public final class ProcessOutAccessor {

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public static ProcessOut initLegacyProcessOut(Application application, String projectId) {
        return new ProcessOut(application, projectId);
    }
}
