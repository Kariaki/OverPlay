package com.overplay.overplay

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.overplay.overplay.Adapter.Listeners.OptionClickListener
import com.overplay.overplay.Adapter.SuperEntity
import com.overplay.overplay.Adapter.ViewHolders.Plugs.OptionPlug
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.database.entities.OptionItem
import com.overplay.overplay.databinding.MusicOptionListDialogItemBinding
import com.overplay.overplay.databinding.MusicOptionListDialogBinding
import com.votenoid.votenoid.Adapter.GeneralAdapter


class MusicOptionModal : BottomSheetDialogFragment() {




    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView=View.inflate(context,R.layout.music_option_list_dialog,null)
        dialog.setContentView(contentView)
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


}