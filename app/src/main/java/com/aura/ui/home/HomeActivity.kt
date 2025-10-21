package com.aura.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aura.R
import com.aura.data.session.SessionManager
import com.aura.databinding.ActivityHomeBinding
import com.aura.ui.login.LoginActivity
import com.aura.ui.transfer.TransferActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The home activity for the app.
 */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    /**
     * The binding for the home layout.
     */
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    /**
     * A callback for the result of starting the TransferActivity.
     */
    private val startTransferActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                updateAccount()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val balance = binding.balance
        val transfer = binding.transfer
        val loading = binding.loading
        val retryButton = binding.retryButton

        updateAccount()

        lifecycleScope.launch {
            viewModel.uiState.collect {
                loading.visibility = if (it.loading) View.VISIBLE else View.GONE
                retryButton.visibility = if (it.isRetryVisible) View.VISIBLE else View.GONE
                if (it.accounts.isNotEmpty()) {
                    balance.text = it.balanceMain.toString()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiMessageFlow.collect {
                Toast.makeText(this@HomeActivity, it.translatedMessage, Toast.LENGTH_SHORT).show()
            }
        }

        retryButton.setOnClickListener {
            updateAccount()
        }

        transfer.setOnClickListener {
            startTransferActivityForResult.launch(
                Intent(
                    this@HomeActivity, TransferActivity::class.java
                )
            )
        }
    }


    private fun updateAccount() {
        lifecycleScope.launch {
            viewModel.getAccount()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.disconnect -> {
                SessionManager.clearSession()
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
