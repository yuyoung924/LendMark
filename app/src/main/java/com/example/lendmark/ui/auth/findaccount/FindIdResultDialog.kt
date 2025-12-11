package com.example.lendmark.ui.auth.findaccount

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.lendmark.R

class FindIdResultDialog(private val email: String?) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.dialog_find_id_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvMessage = view.findViewById<TextView>(R.id.tvDialogMessage)
        val btnConfirm = view.findViewById<Button>(R.id.btnDialogConfirm)

        if (email != null) {
            tvMessage.text = "회원님의 아이디는\n$email\n입니다."
        } else {
            tvMessage.text = "입력된 번호로 가입된 계정을 찾을 수 없습니다."
        }

        btnConfirm.setOnClickListener {
            dismiss()
        }


    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}

