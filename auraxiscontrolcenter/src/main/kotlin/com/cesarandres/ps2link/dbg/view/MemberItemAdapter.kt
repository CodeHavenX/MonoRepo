package com.cesarandres.ps2link.dbg.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cesarandres.ps2link.R
import com.cesarandres.ps2link.dbg.content.Member
import com.cesarandres.ps2link.dbg.content.character.Name
import com.cesarandres.ps2link.module.ObjectDataSource

class MemberItemAdapter(
    private val context: Context,
    outfitId: String,
    data: ObjectDataSource,
    showOffline: Boolean
) : DBItemAdapter() {

    init {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        this.mInflater = LayoutInflater.from(context)
        this.size = data.countAllMembers(outfitId, showOffline)
        this.cursor = data.getMembersCursor(outfitId, showOffline)
    }

    override fun getCount(): Int {
        return this.size
    }

    override fun getItem(position: Int): Member {
        var member: Member
        try {
            member = ObjectDataSource.cursorToMember(
                ObjectDataSource.cursorToPosition(
                    cursor!!,
                    position
                )
            )
        } catch (e: Exception) {
            member = Member()
            member.character_id = ""
            member.online_status = ""
            member.outfit_id = ""
            member.rank = ""
            member.name = object : Name() {
                init {
                    this.first = ""
                }
            }
        }

        return member
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder

        if (convertView == null) {
            convertView = mInflater!!.inflate(R.layout.layout_member_item, parent, false)

            holder = ViewHolder()
            holder.memberName =
                convertView!!.findViewById<View>(R.id.textViewMemberListName) as TextView
            holder.memberStatus =
                convertView.findViewById<View>(R.id.textViewMemberListStatus) as TextView
            holder.memberRank =
                convertView.findViewById<View>(R.id.textViewMemberListRank) as TextView
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        holder.memberName!!.text = getItem(position).name!!.first
        holder.memberRank!!.text = getItem(position).rank

        if (getItem(position).online_status == "0") {
            holder.memberStatus!!.text = context.resources.getString(R.string.text_offline)
            holder.memberStatus!!.setTextColor(Color.RED)
        } else {
            holder.memberStatus!!.text = context.resources.getString(R.string.text_online)
            holder.memberStatus!!.setTextColor(Color.GREEN)
        }

        return convertView
    }

    internal class ViewHolder {
        var memberName: TextView? = null
        var memberStatus: TextView? = null
        var memberRank: TextView? = null
    }
}