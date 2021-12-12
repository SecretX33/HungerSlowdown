package com.github.secretx33.hungerslowdown.eventlistener

import com.github.secretx33.hungerslowdown.manager.HungerSkipManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent
import toothpick.InjectConstructor

@InjectConstructor
class HungerSkipListener(private val hungerManager: HungerSkipManager): Listener {

    @EventHandler(ignoreCancelled = true)
    fun FoodLevelChangeEvent.whenPlayerHungerIsModified() = hungerManager.playerHungerModified(this)
}
