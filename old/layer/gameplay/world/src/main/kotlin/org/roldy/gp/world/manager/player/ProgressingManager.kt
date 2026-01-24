package org.roldy.gp.world.manager.player

import org.roldy.core.coroutines.ConcurrentLoopConsumer

interface ProgressingManager : ConcurrentLoopConsumer<Float> {
}