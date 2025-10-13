package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.aura.R
import kotlinx.coroutines.launch
import com.aura.data.repository.Result

/**
 * The login activity for the app.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

  /**
   * The binding for the login layout.
   */
  private lateinit var binding: ActivityLoginBinding
  private val viewModel: LoginVewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val login = binding.login
    val loading = binding.loading
    val identifier = binding.identifier
    val password = binding.password


    identifier.addTextChangedListener {
      viewModel.onIdentifierChanged(it.toString())
    }

    password.addTextChangedListener {
      viewModel.onPasswordChanged(it.toString())
    }

    lifecycleScope.launch {
      viewModel.isLoginEnabled.collect { login.isEnabled = it  }
    }

    login.setOnClickListener {
      lifecycleScope.launch {
        viewModel.login()

        viewModel.loginState.collect {
          loading.visibility = if (it.loading) View.VISIBLE else View.GONE

          if (it.loginResult == true) {
            Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()

            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
          }
          if (it.loginResult == false) {
            Toast.makeText(this@LoginActivity, getString(R.string.login_refused), Toast.LENGTH_SHORT).show()
          }
          if (it.errorMessage != null) {
            Toast.makeText(this@LoginActivity, getString(it.errorMessage), Toast.LENGTH_SHORT).show()
          }
        }
      }

    }


  }
}