package com.processout.sdk.api.network.exception

import com.processout.sdk.core.exception.ProcessOutException

class ValidationException(val code: Int, message: String) : ProcessOutException(message)
