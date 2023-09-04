package com.processout.processout_sdk;

import android.app.Application;

import com.processout.sdk.core.annotation.ProcessOutInternalApi;

/**
 * @hide
 * @noinspection removal
 */
@ProcessOutInternalApi
public final class ProcessOutAccessor {

    private ProcessOutAccessor() {
        // private constructor
    }

    public static ProcessOut initLegacyProcessOut(Application application, String projectId) {
        return new ProcessOut(application, projectId);
    }
}
