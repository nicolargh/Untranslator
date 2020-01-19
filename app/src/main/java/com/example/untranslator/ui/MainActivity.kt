package com.example.untranslator.ui

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.untranslator.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        untranslate_button.setOnClickListener {
            val toTranslate = read_text.text.toString()

            // translate

            untranslated_text.text = toTranslate
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // hide keyboard on click away from edit text
        currentFocus?.let {view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}
