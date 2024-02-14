package com.processout.processout_sdk;

import com.processout.sdk.core.annotation.ProcessOutInternalApi;
import com.processout.sdk.di.ContextGraph;

/**
 * @hide
 * @noinspection removal
 */
@ProcessOutInternalApi
public final class ProcessOutLegacyAccessor {

    private ProcessOutLegacyAccessor() {
        // private constructor
    }

    public static ProcessOut configure(ContextGraph contextGraph) {
        return new ProcessOut(contextGraph);
    }
}
