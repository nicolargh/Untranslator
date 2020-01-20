package com.example.untranslator.ui

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.untranslator.R
import com.example.untranslator.data.LanguageCode
import com.example.untranslator.model.MainViewModel
import com.example.untranslator.model.TranslationAction
import com.example.untranslator.model.TranslationViewState
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = MainViewModel.Factory().getViewModel(this)
        viewModel.liveData.observe(this, Observer {
            if (it.progress == it.numTranslations) {
                progress_bar.visibility = View.GONE
                progress_text.visibility = View.GONE

                untranslated_text.text = when {
                    it.toText.isNotEmpty() -> it.toText
                    it.error == null -> ""
                    else -> getString(if (it.error is TranslationViewState.Error.Api) R.string.api_error else R.string.error)
                }
            } else {
                progress_bar.visibility = View.VISIBLE
                progress_text.visibility = View.VISIBLE

                progress_bar.max = it.numTranslations
                progress_bar.progress = it.progress
                progress_text.text =
                    getString(R.string.translated_through, it.toCode.toString(), it.toText)
                untranslated_text.text = ""
            }
        })

        untranslate_button.setOnClickListener {
            val toTranslate = read_text.text.toString()

            viewModel.dispatchTranslation(
                TranslationAction(
                    fromCode = LanguageCode.English,
                    toCode = LanguageCode.English,
                    text = toTranslate,
                    numTranslations = 5
                )
            )
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // hide keyboard on click away from edit text
        currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}
