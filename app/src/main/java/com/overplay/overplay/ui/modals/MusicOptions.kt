package com.overplay.overplay.ui.modals

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.overplay.overplay.R


class MusicOptions : BottomSheetDialogFragment() {


    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = View.inflate(context, R.layout.music_options, null)
        dialog.setContentView(contentView)
        (contentView.parent as View).setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}