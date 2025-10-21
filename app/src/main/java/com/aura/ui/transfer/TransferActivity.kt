package com.aura.ui.transfer

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.aura.databinding.ActivityTransferBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The transfer activity for the app.
 */
@AndroidEntryPoint
class TransferActivity : AppCompatActivity() {

    /**
     * The binding for the transfer layout.
     */
    private lateinit var binding: ActivityTransferBinding
    private val viewModel: TransferViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recipient = binding.recipient
        val amount = binding.amount
        val transfer = binding.transfer
        val loading = binding.loading

        recipient.addTextChangedListener {
            viewModel.onRecipientChanged(it.toString())
        }

        amount.addTextChangedListener {
            viewModel.onAmountChanged(it.toString().toDouble())
        }

        lifecycleScope.launch {
            viewModel.transferState.collect { transfer.isEnabled = it.isTransferEnabled }
        }

        lifecycleScope.launch {
            viewModel.uiMessage.collect {
                Log.d("TransferActivity", "Message received: $it")
                if (it.errorMessage != null) {
                    Toast.makeText(
                        this@TransferActivity,
                        it.errorMessage.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (it.type != null)
                        Toast.makeText(
                            this@TransferActivity,
                            it.type.translatedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.transferState.collect {
                loading.visibility = if (it.isLoading) View.VISIBLE else View.GONE

                if (it.isTransferGranted == true) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }


        transfer.setOnClickListener {
            lifecycleScope.launch {
                viewModel.transfer()
            }
        }

    }

}
