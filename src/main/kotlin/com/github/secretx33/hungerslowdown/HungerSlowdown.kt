package com.github.secretx33.hungerslowdown

import com.github.secretx33.hungerslowdown.config.HungerConfig
import com.github.secretx33.hungerslowdown.eventlistener.HungerSkipListener
import com.github.secretx33.sccfg.getConfig
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import toothpick.Scope
import toothpick.Toothpick
import toothpick.configuration.Configuration
import toothpick.ktp.KTP
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module
import kotlin.reflect.KClass

class HungerSlowdown : JavaPlugin() {

    private val mod = module {
        bind<Plugin>().toInstance(this@HungerSlowdown)
        bind<JavaPlugin>().toInstance(this@HungerSlowdown)
        bind<HungerConfig>().toProviderInstance { getConfig<HungerConfig>() }
    }

    private lateinit var scope: Scope

    override fun onEnable() {
        Toothpick.setConfiguration(Configuration.forDevelopment())
        scope = KTP.openScope("HungerSlowdown").installModules(mod)
        registerListeners(HungerSkipListener::class)
    }

    override fun onDisable() {
        KTP.closeScope("HungerSlowdown")
    }

    private fun registerListeners(vararg listeners: KClass<out Listener>) {
        listeners.map { scope.getInstance(it.java) }
            .forEach { Bukkit.getPluginManager().registerEvents(it, this) }
    }
}
