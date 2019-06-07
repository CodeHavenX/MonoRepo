package com.cramsan.petproject.storage

interface ModelStorageInterface {
    fun registerListener(listener: ModelStorageListenerInterface)
    fun deregisterListener(listener: ModelStorageListenerInterface)
    fun getPlants(forceUpdate: Boolean)
    fun getItems(forceUpdate: Boolean)
}