package com.aura.ui.transfer

import android.app.Activity
import android.os.Bundle
import android.view.View
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
            viewModel.onAmountChanged(it.toString())
        }


        lifecycleScope.launch {
            viewModel.transferState.collect { transfer.isEnabled = it.isTransferEnabled }
        }


        transfer.setOnClickListener {
            loading.visibility = View.VISIBLE

            setResult(Activity.RESULT_OK)
            finish()
        }


    }

}
