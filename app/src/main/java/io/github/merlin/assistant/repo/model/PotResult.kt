package io.github.merlin.assistant.repo.model

import io.github.merlin.assistant.data.network.response.QueryExchangeResponse

sealed class QueryExchangeResult {

    data class Success(val exchange: List<QueryExchangeResponse.Exchange>) : QueryExchangeResult()
    data class Error(val msg: String): QueryExchangeResult()

}