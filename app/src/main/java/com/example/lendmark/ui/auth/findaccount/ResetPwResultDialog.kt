package com.example.lendmark.ui.auth.findaccount

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.lendmark.R

class ResetPwResultDialog(private val message: String) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 배경 투명하게 해서 둥근 모서리 레이아웃이 잘 보이도록 설정
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 우리가 만든 레이아웃 inflate
        return inflater.inflate(R.layout.dialog_reset_pw_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvMessage = view.findViewById<TextView>(R.id.tvDialogMessage)
        val btnConfirm = view.findViewById<Button>(R.id.btnDialogConfirm)

        // 전달받은 메시지 설정
        tvMessage.text = message

        // 확인 버튼 클릭 → Dialog 닫기
        btnConfirm.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        // Dialog 크기 조절 (화면의 90% 정도)
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}

