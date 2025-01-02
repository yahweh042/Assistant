package io.github.merlin.assistant.data.network.response

import kotlinx.serialization.Serializable

@Serializable
data class GetAwardResponse(
    val result: Int,
    val msg: String?,
    val changed: Changed?,
) {

    val award
        get() = run { changed?.items?.joinToString(" ") { "${it.name}*${it.num}" } ?: if (result == 0) "ok" else msg }

}