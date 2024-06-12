package com.processout.sdk.configuration

import androidx.test.core.app.ApplicationProvider
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class SetupRule : TestRule {

    override fun apply(base: Statement, description: Description) =
        object : Statement() {
            override fun evaluate() {
                TestConfiguration.configure(
                    ApplicationProvider.getApplicationContext()
                )
                base.evaluate()
            }
        }
}
