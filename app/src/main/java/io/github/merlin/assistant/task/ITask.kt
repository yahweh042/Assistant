package io.github.merlin.assistant.task

interface ITask {

    val taskName: String

    suspend fun run()

}