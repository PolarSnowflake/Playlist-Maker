package com.example.playlist_maker.ui.media_library.playlists

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.playlist_maker.R

class ExitDialogFragment : DialogFragment() {

    private var positiveClickListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.exit_creation_title))
            .setMessage(getString(R.string.exit_creation_message))
            .setPositiveButton(getString(R.string.finish)) { _, _ ->
                positiveClickListener?.invoke()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    fun setPositiveClickListener(listener: () -> Unit) {
        positiveClickListener = listener
    }

    companion object {
        fun newInstance(): ExitDialogFragment {
            return ExitDialogFragment()
        }
    }
}