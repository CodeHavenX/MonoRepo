package com.cramsan.petproject.plantslist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.classTag
import com.cramsan.petproject.R
import com.cramsan.petproject.appcore.model.AnimalType
import com.cramsan.petproject.appcore.model.PresentablePlant
import com.cramsan.petproject.appcore.model.ToxicityValue
import kotlinx.android.synthetic.main.view_plant.view.plant_list_view_header
import kotlinx.android.synthetic.main.view_plant.view.plant_list_view_layout
import kotlinx.android.synthetic.main.view_plant.view.plant_list_view_sub_header
import org.kodein.di.KodeinAware
import org.kodein.di.erased.instance

class PlantsRecyclerViewAdapter(
    private val mListener: OnListFragmentAdapterListener?,
    private val animalType: AnimalType,
    context: Context
) :
    RecyclerView.Adapter<PlantsRecyclerViewAdapter.ViewHolder>(), KodeinAware {

    override val kodein by org.kodein.di.android.kodein(context)
    private val eventLogger: EventLoggerInterface by instance()

    private var mValues: List<PresentablePlant> = listOf()
    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as PresentablePlant
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.

            mListener?.onNewItemSelected(item.plantId.toInt(), animalType)
        }
    }

    fun updateValues(values: List<PresentablePlant>) {
        eventLogger.log(Severity.VERBOSE, classTag(), "updateValues")
        mValues = values
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        eventLogger.log(Severity.DEBUG, classTag(), "onCreateViewHolder")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_plant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        eventLogger.log(Severity.VERBOSE, classTag(), "onBindViewHolder")
        val item = mValues[position]
        holder.mViewHeader.text = item.mainCommonName
        holder.mViewSubHeader.text = item.scientificName
        when (item.isToxic) {
            ToxicityValue.TOXIC -> holder.mContainerView.setBackgroundResource(R.drawable.plant_view_item_danger)
            ToxicityValue.NON_TOXIC -> holder.mContainerView.setBackgroundResource(R.drawable.plant_view_item_safe)
            ToxicityValue.UNDETERMINED -> holder.mContainerView.setBackgroundResource(R.drawable.plant_view_item_undetermined)
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mViewHeader: TextView = mView.plant_list_view_header
        val mViewSubHeader: TextView = mView.plant_list_view_sub_header
        val mContainerView: View = mView.plant_list_view_layout

        override fun toString(): String {
            return super.toString() + " '" + mViewHeader.text + "'"
        }
    }

    interface OnListFragmentAdapterListener {
        fun onNewItemSelected(plantId: Int, animalType: AnimalType)
    }
}
