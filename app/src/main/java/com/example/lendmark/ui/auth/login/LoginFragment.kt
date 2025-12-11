// LoginFragment.kt
package com.example.lendmark.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.lendmark.R
import com.example.lendmark.databinding.FragmentLoginBinding
import com.example.lendmark.ui.main.MainActivity

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로그인 버튼
        binding.btnLogin.setOnClickListener { doLogin() }

        // 키보드의 "Done"으로도 로그인
        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doLogin()
                true
            } else false
        }

        // 관찰자: 성공/실패 메시지는 Event 래퍼로 1회만 표시
        viewModel.loginResult.observe(viewLifecycleOwner) { event ->
            val uid = event.getContentIfNotHandled()

            if (uid != null) {
                // 로그인 성공 → Firestore 플래그 검사
                viewModel.checkMustChangePassword(uid)
            }
        }

        viewModel.mustChangePassword.observe(viewLifecycleOwner) { needChange ->
            if (needChange) {
                // 🔥 임시 비번 로그인 → 비밀번호 변경 화면으로 강제 이동
                findNavController().navigate(R.id.action_login_to_changePassword)
            } else {
                // 정상 로그인 → 메인 화면 이동
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                requireActivity().finish()
            }
        }



        viewModel.errorMessage.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        // (선택) 회원가입/계정찾기 이동이 있으면 연결
        binding.tvSignup?.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signup)
        }
        binding.tvFindAccount?.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_findAccount)
        }
    }

    private fun doLogin() {
        val emailId = binding.etEmailId.text?.toString()?.trim().orEmpty()
        val email = "$emailId@seoultech.ac.kr"
        val pw = binding.etPassword.text?.toString()?.trim().orEmpty()
        viewModel.login(email, pw)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
