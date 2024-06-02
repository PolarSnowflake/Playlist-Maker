package com.example.playlist_maker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

            val button1: Button = findViewById(R.id.button1)
            val button2: Button = findViewById(R.id.button2)
            val button3: Button = findViewById(R.id.button3)

            //  реализация анонимного класса
            button1.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    Toast.makeText(this@MainActivity, "Нажата кнопка Поиск", Toast.LENGTH_SHORT).show()
                }
            })
            //  реализация лямбда-выражения
            button2.setOnClickListener {
                Toast.makeText(this, "Нажата кнопка  Медиатека", Toast.LENGTH_SHORT).show()
            }

            button3.setOnClickListener {
                Toast.makeText(this, "Нажата кнопка Настройки", Toast.LENGTH_SHORT).show()
            }

            insets
        }
    }
}