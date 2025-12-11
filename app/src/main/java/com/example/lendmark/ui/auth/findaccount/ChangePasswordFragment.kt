package com.example.lendmark.ui.auth.findaccount

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.lendmark.databinding.FragmentChangePasswordBinding
import com.example.lendmark.ui.main.MainActivity
import com.example.lendmark.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack?.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnChangePassword.setOnClickListener {
            val newPw = binding.etNewPassword.text.toString().trim()
            val confirmPw = binding.etConfirmPassword.text.toString().trim()

            if (newPw.length < 6) {
                showToast("비밀번호는 6자 이상이어야 합니다.")
                return@setOnClickListener
            }
            if (newPw != confirmPw) {
                showToast("비밀번호가 일치하지 않습니다.")
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user == null) {
                showToast("로그인 정보가 없습니다. 다시 로그인해주세요.")
                findNavController().navigateUp()
                return@setOnClickListener
            }

            user.updatePassword(newPw)
                .addOnSuccessListener {
                    db.collection("users").document(user.uid)
                        .update("mustChangePassword", false)
                        .addOnSuccessListener {
                            showToast("비밀번호가 성공적으로 변경되었습니다.")

                            // 🔥 MainActivity로 이동
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            intent.addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            )
                            startActivity(intent)
                            requireActivity().finish()
                        }
                        .addOnFailureListener { e ->
                            showToast("변경 성공했지만 저장 오류 발생: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    showToast("비밀번호 변경 실패: ${e.message}")
                }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
