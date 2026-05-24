package com.cramsan.architecture.client.settings

/**
 * Central registry for all setting groups contributed by any application module.
 *
 * Each module calls [register] once during DI startup to make its settings available for
 * enumeration and display in the debug settings UI.
 *
 * Groups are returned in the order they were registered.
 */
interface SettingRegistry {
    /**
     * Returns all registered setting groups in insertion order.
     */
    val groups: List<SettingGroup>

    /**
     * Registers one or more [SettingGroup]s contributed by a module.
     *
     * Intended to be called once per module during DI initialisation, before the UI renders.
     *
     * @param groups One or more [SettingGroup] instances to add to the registry.
     */
    fun register(vararg groups: SettingGroup)
}

/**
 * Default mutable implementation of [SettingRegistry] backed by an ordered list.
 *
 * Thread safety: registration happens synchronously during Koin startup on the main thread,
 * before any coroutines or composables execute, so no locking is required.
 */
class SettingRegistryImpl : SettingRegistry {
    private val _groups = mutableListOf<SettingGroup>()

    override val groups: List<SettingGroup>
        get() = _groups.toList()

    override fun register(vararg groups: SettingGroup) {
        _groups.addAll(groups)
    }
}
