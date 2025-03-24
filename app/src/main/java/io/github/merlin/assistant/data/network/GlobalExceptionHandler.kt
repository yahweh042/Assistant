package io.github.merlin.assistant.data.network

import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class GlobalExceptionHandlerConfig

@OptIn(InternalAPI::class)
val GlobalExceptionHandler = createClientPlugin(
    name = "GlobalExceptionHandler",
    createConfiguration = ::GlobalExceptionHandlerConfig,
) {
    on(Send) { request ->
        try {
            proceed(request)
        } catch (e: Exception) {
            val jsonObject = buildJsonObject {
                put("result", -1)
                put("msg", "$e")
            }
            val httpResponseData = HttpResponseData(
                statusCode = HttpStatusCode.OK,
                requestTime = GMTDate(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Html.toString()),
                version = HttpProtocolVersion.HTTP_2_0,
                body = ByteReadChannel(jsonObject.toString()),
                callContext = request.executionContext
            )
            HttpClientCall(client, request.build(), httpResponseData)
        }
    }

}