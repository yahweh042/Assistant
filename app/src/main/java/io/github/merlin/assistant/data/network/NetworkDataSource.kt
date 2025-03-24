package io.github.merlin.assistant.data.network

import io.github.merlin.assistant.repo.AccountRepo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.ParametersBuilder
import javax.inject.Inject

class NetworkDataSource @Inject constructor(
    val httpClient: HttpClient,
    val accountRepo: AccountRepo,
) {

    val noAuthCmd = setOf("index")

    suspend inline fun <reified T> request(cmd: String, params: Map<String, Any> = mapOf()): T {
        val httpRequestBuilder = HttpRequestBuilder()
        httpRequestBuilder.url("https://zone1.ledou.qq.com/fcgi-bin/petpk")
        httpRequestBuilder.method = HttpMethod.Post
        httpRequestBuilder.header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded)
        httpRequestBuilder.header(HttpHeaders.Origin, "https://res.ledou.qq.com")
        httpRequestBuilder.header(HttpHeaders.Referrer, "https://res.ledou.qq.com")
        httpRequestBuilder.header(HttpHeaders.Accept, ContentType.Any)
        val parametersBuilder = ParametersBuilder()
        parametersBuilder.append("cmd", cmd)
        params.forEach { (key, value) -> parametersBuilder.append(key, value.toString()) }
        parametersBuilder.append("uin", "")
        parametersBuilder.append("skey", "")
        if (!noAuthCmd.contains(cmd)) {
            accountRepo.accountState?.activeAccount?.let {
                parametersBuilder.append("uid", it.uid)
                parametersBuilder.append("h5openid", it.openid)
                parametersBuilder.append("h5token", it.token)
            }
        }
        parametersBuilder.append("pf", "sq")
        parametersBuilder.append("from", "1")

        val parameters = parametersBuilder.build()
        httpRequestBuilder.setBody(FormDataContent(parameters))
        return httpClient.request(httpRequestBuilder).body<T>()
    }

}
