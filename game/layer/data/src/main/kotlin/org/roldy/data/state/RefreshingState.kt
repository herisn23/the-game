package org.roldy.data.state

import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


@Serializable
data class RefreshingState(
    var max: Int = 10,
    var supplies: Int = 10,
    var timeToRefresh: Duration = 5.seconds,
    var currentRefreshTime: Duration = 0.seconds
)
