package com.github.secretx33.hungerslowdown.config

import com.github.secretx33.sccfg.api.Naming
import com.github.secretx33.sccfg.api.annotation.AfterReload
import com.github.secretx33.sccfg.api.annotation.Configuration
import com.github.secretx33.sccfg.api.annotation.Name
import com.github.secretx33.sccfg.api.annotation.Path

@Configuration("config", naming = Naming.LOWERCASE_HYPHENATED)
class HungerConfig {
    @Path("hunger-slowdown-percentage")
    @Name("normal")
    var hungerSlowdownPercentage = 0.35
    @Path("hunger-slowdown-percentage")
    @Name("when-healing")
    var hungerSlowdownPercentageWhenHealing = 0.5
    val foodBoostMultiplierPercentage = 1.0

    @AfterReload(async = true)
    private fun ensureLimits() {
        hungerSlowdownPercentage = hungerSlowdownPercentage.coerceIn(0.0, 1.0)
        hungerSlowdownPercentageWhenHealing = hungerSlowdownPercentageWhenHealing.coerceIn(0.0, 1.0)
    }
}
