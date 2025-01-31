package io.github.merlin.assistant.ui.base

interface Option<T> {
    val value: T
    val label: String
}

fun <V, T : Option<V>> Array<T>.findOption(value: V): T {
    return find { it.value == value } ?: first()
}

enum class PotEquipFindOption(override val value: Int, override val label: String): Option<Int> {
    NotFind(0, "未配置"),
    PointFind(1,"战力优先"),
    AttrFind(2, "词条优先"),
}
