package com.processout.sdk.api.network.exception

import com.processout.sdk.core.exception.ProcessOutException

class ServerException(val code: Int, message: String) : ProcessOutException(message)
