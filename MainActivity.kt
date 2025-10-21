package com.example.superapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.superapp.ui.theme.SuperAppTheme
import android.content.Intent
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //toolbar
        setSupportActionBar(findViewById(R.id.toolbar))

        //cal clicl
        findViewById<LinearLayout>(R.id.cardCalculator).setOnClickListener {
            val intent = Intent(this, CalculatorActivity::class.java)
            startActivity(intent)
        }

        //notepad click
        findViewById<LinearLayout>(R.id.cardNotepad).setOnClickListener {
            val intent = Intent(this, NotepadActivity::class.java)
            startActivity(intent)
        }
    }
}