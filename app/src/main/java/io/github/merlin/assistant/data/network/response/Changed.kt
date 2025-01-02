package io.github.merlin.assistant.data.network.response

import kotlinx.serialization.Serializable

@Serializable
data class Changed(
    val items: List<ChangedItem>?,
)