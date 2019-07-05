package com.cramsan.petproject.plantdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.petproject.appcore.framework.CoreFrameworkAPI
import com.cramsan.petproject.appcore.model.AnimalType
import com.cramsan.petproject.appcore.model.Plant
import com.cramsan.petproject.appcore.model.PlantMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlantDetailsViewModel : ViewModel() {

    private val modelStore = CoreFrameworkAPI.modelStorage

    private val observablePlant = MutableLiveData<Plant>()
    private val observablePlantMetadata = MutableLiveData<PlantMetadata>()

    fun reloadPlant(plantId: Int) {
        viewModelScope.launch {
            loadPlant(plantId)
        }
    }

    fun getPlant(): LiveData<Plant> {
        return observablePlant
    }

    fun getPlantMetadata(): LiveData<PlantMetadata> {
        return observablePlantMetadata
    }

    private suspend fun loadPlant(plantId: Int) = withContext(Dispatchers.IO) {
        val plant = modelStore.getPlant(plantId)
        val plantMetadata = modelStore.getPlantMetadata(AnimalType.CAT, plantId)
        viewModelScope.launch {
            observablePlant.value = plant
            observablePlantMetadata.value = plantMetadata
        }
    }
}
