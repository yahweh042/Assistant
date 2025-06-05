package io.github.merlin.assistant.task

import javax.inject.Inject

class MeridianTask @Inject constructor(

): ITask {

    override val taskName: String
        get() = "meridian"

    override suspend fun run() {

    }

}