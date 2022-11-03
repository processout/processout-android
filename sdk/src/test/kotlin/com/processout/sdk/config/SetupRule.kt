package com.processout.sdk.config

import androidx.test.core.app.ApplicationProvider
import com.processout.sdk.core.exception.ProcessOutException
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class SetupRule : TestRule {

    override fun apply(base: Statement, description: Description) =
        object : Statement() {
            override fun evaluate() {
                try {
                    TestConfiguration.configure(
                        ApplicationProvider.getApplicationContext()
                    )
                } catch (_: ProcessOutException) {
                    // ignore
                }
                base.evaluate()
            }
        }
}
