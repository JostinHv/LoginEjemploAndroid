package com.jostin.loginexample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.jostin.loginexample.R
import com.jostin.loginexample.data.AppDatabase
import com.jostin.loginexample.data.UserRepository
import com.jostin.loginexample.databinding.FragmentLoginBinding
import com.jostin.loginexample.viewmodel.AuthViewModel
import com.jostin.loginexample.viewmodel.AuthViewModelFactory

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel
    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa el DAO y el repository
        val userDao = AppDatabase.getDatabase(requireContext()).userDao()
        userRepository = UserRepository(userDao)

        // Usa la factory para obtener el ViewModel
        val factory = AuthViewModelFactory(userRepository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        binding.loginButton.setOnClickListener {


            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password) { success ->
                    if (success) {
                        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
                            if (user != null) {
                                if (findNavController().currentDestination?.id != R.id.welcomeFragment) {
                                    findNavController().navigate(R.id.action_loginFragment_to_welcomeFragment)
                                }
                            }
                        }
                    } else {
                        Snackbar.make(
                            binding.root,
                            "Login failed. Please try again.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                Snackbar.make(binding.root, "Please fill all fields", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}