package com.example.lendmark.ui.auth.findaccount

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.lendmark.databinding.FragmentFindAccountBinding
import com.example.lendmark.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions

class FindAccountFragment : Fragment() {

    private lateinit var binding: FragmentFindAccountBinding
    private val authViewModel: AuthViewModel by viewModels()

    private val db = FirebaseFirestore.getInstance()   // 🔥 Firestore 추가
    private val functions = FirebaseFunctions.getInstance("asia-northeast3")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFindAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음 화면 → Find ID 기본 선택
        binding.tabToggleGroup.check(binding.btnFindIdTab.id)

        // 🔙 뒤로가기
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // 탭 전환
        binding.tabToggleGroup.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                binding.btnFindIdTab.id -> {
                    binding.layoutFindId.visibility = View.VISIBLE
                    binding.layoutFindPw.visibility = View.GONE
                }
                binding.btnFindPwTab.id -> {
                    binding.layoutFindId.visibility = View.GONE
                    binding.layoutFindPw.visibility = View.VISIBLE
                }
            }
        }

        // --------------------------------------------------------
        // 🔎 [1] 전화번호로 이메일(ID) 찾기
        // --------------------------------------------------------
        binding.btnFindId.setOnClickListener {

            val rawPhone = binding.etPhone.text.toString().trim()
            if (rawPhone.isEmpty()) {
                showIdDialog("전화번호를 입력해주세요.")
                return@setOnClickListener
            }

            val phone = formatPhoneNumber(rawPhone)
            Log.d("FindAccount", "Formatted phone = $phone")

            authViewModel.findEmailByPhone(phone)
        }

        authViewModel.foundEmail.observe(viewLifecycleOwner) { email ->
            FindIdResultDialog(email).show(parentFragmentManager, "FindIdResultDialog")
        }


        // --------------------------------------------------------
        // 🔐 [2] 이메일로 임시 비밀번호 발송
        // --------------------------------------------------------
        binding.btnSendResetLink.setOnClickListener {

            val email = binding.etEmailForPw.text.toString().trim().lowercase()

            if (email.isEmpty()) {
                showDialog("이메일을 입력해주세요.")
                return@setOnClickListener
            }

            Log.d("FindAccount", "입력한 이메일 = $email")

            // 1) Firestore에 이메일 존재하는지 확인
            db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener { snap ->
                    if (snap.isEmpty) {
                        showDialog("가입된 이메일이 아닙니다.")
                    } else {
                        sendResetRequest(email)
                    }
                }
                .addOnFailureListener { e ->
                    showDialog("오류 발생: ${e.message}")
                }

        }
    }

    // --------------------------------------------------------
    // [Cloud Function] 임시 비밀번호 요청
    // --------------------------------------------------------
    private fun sendResetRequest(email: String) {

        Log.d("FindAccount", "임시 비밀번호 발송 요청 email = $email")

        functions
            .getHttpsCallable("sendTempPassword")
            .call(mapOf("email" to email))
            .addOnSuccessListener {
                Log.d("FindAccount", "임시 비밀번호 발송 성공")

                showDialog(
                    "임시 비밀번호를\n$email\n으로 전송했습니다.\n\n로그인 후 반드시 변경하세요."
                )
            }
            .addOnFailureListener { e ->
                Log.e("FindAccount", "임시 비밀번호 발송 실패: ${e.message}")

                val msg = when {
                    e.message?.contains("NOT_FOUND") == true ->
                        "가입된 이메일이 아닙니다."
                    else ->
                        "임시 비밀번호 발송 실패: ${e.message}"
                }

                showDialog(msg)
            }
    }

    // --------------------------------------------------------
    // 📌 전화번호 010-XXXX-XXXX 변환
    // --------------------------------------------------------
    private fun formatPhoneNumber(input: String): String {
        val digits = input.filter { it.isDigit() }
        return if (digits.length == 11) {
            "${digits.substring(0, 3)}-${digits.substring(3, 7)}-${digits.substring(7, 11)}"
        } else input
    }

    // --------------------------------------------------------
    // 📌 공통 다이얼로그
    // --------------------------------------------------------
    private fun showDialog(message: String) {
        ResetPwResultDialog(message).show(parentFragmentManager, "ResetPwDialog")
    }

    private fun showIdDialog(message: String) {
        FindIdResultDialog(null).show(parentFragmentManager, "FindIdResultDialog")
    }
}
