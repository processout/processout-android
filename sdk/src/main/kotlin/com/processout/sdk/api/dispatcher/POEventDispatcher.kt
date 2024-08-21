package com.processout.sdk.api.dispatcher

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import java.util.UUID

/** @suppress */
@ProcessOutInternalApi
object POEventDispatcher {

    interface Request {
        val uuid: UUID
    }

    interface Response {
        val uuid: UUID
    }

    private val _events = MutableSharedFlow<Any>()
    val events = _events.asSharedFlow()

    private val _requests = MutableSharedFlow<Request>()
    val requests = _requests.asSharedFlow()

    private val _responses = MutableSharedFlow<Response>()
    val responses = _responses.asSharedFlow()

    suspend fun send(event: Any) {
        _events.emit(event)
    }

    suspend fun send(request: Request) {
        _requests.emit(request)
    }

    suspend fun send(response: Response) {
        _responses.emit(response)
    }

    inline fun <reified T : Any> subscribe(
        coroutineScope: CoroutineScope,
        crossinline onEvent: (T) -> Unit
    ) {
        coroutineScope.launch {
            events.filterIsInstance<T>()
                .collect { event ->
                    coroutineContext.ensureActive()
                    onEvent(event)
                }
        }
    }

    inline fun <reified T : Request> subscribeForRequest(
        coroutineScope: CoroutineScope,
        crossinline onRequest: (T) -> Unit
    ) {
        coroutineScope.launch {
            requests.filterIsInstance<T>()
                .collectLatest { request ->
                    coroutineContext.ensureActive()
                    onRequest(request)
                }
        }
    }

    inline fun <reified T : Response> subscribeForResponse(
        coroutineScope: CoroutineScope,
        crossinline onResponse: (T) -> Unit
    ) {
        coroutineScope.launch {
            responses.filterIsInstance<T>()
                .collectLatest { response ->
                    coroutineContext.ensureActive()
                    onResponse(response)
                }
        }
    }
}
