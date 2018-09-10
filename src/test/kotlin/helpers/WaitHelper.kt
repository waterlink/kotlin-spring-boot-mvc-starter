package helpers

import org.fluentlenium.core.FluentControl

interface WaitHelper : FluentControl {
    fun awaitAtMostFor(milliseconds: Long, block: () -> Unit) {
        val oldAwaitAtMost = awaitAtMost
        awaitAtMost = milliseconds
        block()
        awaitAtMost = oldAwaitAtMost
    }
}
