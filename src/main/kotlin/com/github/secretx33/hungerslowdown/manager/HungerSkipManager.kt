package com.github.secretx33.hungerslowdown.manager

import com.github.secretx33.hungerslowdown.config.HungerConfig
import com.google.common.cache.CacheBuilder
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.FoodLevelChangeEvent
import toothpick.InjectConstructor
import java.util.Random
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Singleton
@InjectConstructor
class HungerSkipManager(private val config: HungerConfig) {
    //private val logger = plugin.logger

    /**
     * Holds from what time onwards a hunger tick can happen again, using the entity unique id as key.
     */
    private val cancelledHungerTickCache = CacheBuilder.newBuilder()
        .expireAfterWrite(SKIP_HUNGER_TICK_GRACE_PERIOD, TimeUnit.MILLISECONDS)
        .build<UUID, Long>()

    fun playerHungerModified(event: FoodLevelChangeEvent) {
        val entity = event.entity as? Player ?: return
        val foodLevelModification = event.foodLevel - entity.foodLevel
        //logger.info("Hunger level of player ${entity.name} (currentFoodLevel = ${entity.foodLevel}) has changed, item = $item, eventFoodLevel = $foodLevel")

        if (foodLevelModification < 0) {
            event.playerHungerHasLowered()
        } else if (foodLevelModification > 0) {
            event.playerHungerHasIncreased(foodLevelModification)
        }
    }

    private fun FoodLevelChangeEvent.playerHungerHasLowered() {
        val entity = entity as? Player ?: return
        if (entity.foodLevel >= 18 && entity.health < 19.5) {
            // food level is lowering because player is healing
            playerHungerLoweredHealing()
        } else {
            // food level is lowering because player is doing something else (running, jumping, etc)
            whenPlayerHungerLowersNormal()
        }
    }

    private fun FoodLevelChangeEvent.playerHungerLoweredHealing() {
        if (!entity.willSkipHungerTick(config.hungerSlowdownPercentageWhenHealing)) return
        isCancelled = true
        //logger.info("Cancelled hunger event for player ${entity.name} this time (HEALING), hungerSlowdownPercentageWhenHealing = ${config.hungerSlowdownPercentageWhenHealing}, currentHealth = ${entity.health}")
    }

    private fun FoodLevelChangeEvent.whenPlayerHungerLowersNormal() {
        if (!entity.willSkipHungerTick(config.hungerSlowdownPercentage)) return
        isCancelled = true
        //logger.info("Cancelled hunger event for player ${entity.name} this time, hungerSlowdownPercentage = ${config.hungerSlowdownPercentage}")
    }

    private fun FoodLevelChangeEvent.playerHungerHasIncreased(amount: Int) {
        val entity = entity as? Player ?: return
        foodLevel = entity.foodLevel + (amount.toDouble() * config.foodBoostMultiplierPercentage).toInt()
    }

    private fun HumanEntity.willSkipHungerTick(chance: Double): Boolean {
        cancelledHungerTickCache.getIfPresent(uniqueId)?.let { return it > System.currentTimeMillis() }
        if (random.nextDouble() > chance) return false
        cancelledHungerTickCache.put(uniqueId, System.currentTimeMillis() + SKIP_HUNGER_TICK_GRACE_PERIOD)
        return true
    }

    private companion object {
        val random = Random()
        const val SKIP_HUNGER_TICK_GRACE_PERIOD = 3100L
    }
}
