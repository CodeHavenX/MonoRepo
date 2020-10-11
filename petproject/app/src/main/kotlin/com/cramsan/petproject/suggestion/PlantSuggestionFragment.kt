package com.cramsan.petproject.suggestion

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.cramsan.petproject.R
import com.cramsan.petproject.base.BaseDialogFragment
import com.cramsan.petproject.databinding.FragmentPlantSuggestionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlantSuggestionFragment : BaseDialogFragment<PlantSuggestionViewModel, FragmentPlantSuggestionBinding>() {

    override val contentViewLayout: Int
        get() = R.layout.fragment_plant_suggestion
    override val logTag: String
        get() = "PlantSuggestionFragment"

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val animalTypeId = activity?.intent?.getIntExtra(ANIMAL_TYPE, -1) ?: return

        val model: PlantSuggestionViewModel by viewModels()
        dataBinding.viewModel = model
        model.observableIsComplete.observe(
            viewLifecycleOwner,
            Observer {
                if (it.suggestionSubmitted) {
                    Toast.makeText(context, R.string.thanks_suggestion, Toast.LENGTH_LONG).show()
                }
                closeDialog()
            }
        )
        viewModel = model
    }

    private fun closeDialog() {
        val action = PlantSuggestionFragmentDirections.actionPlantSuggestionFragmentPop()
        findNavController().navigate(action)
    }

    companion object {
        const val ANIMAL_TYPE = "animalType"
    }
}