package com.example.findkard_vodoppianov

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val backgroundImageView: ImageView = findViewById(R.id.backgroundImageView)
        val animatedTextView: TextView = findViewById(R.id.animatedTextView)
        val startGameButton: Button = findViewById(R.id.startGameButton)

        // Установка изображения фона из ресурсов drawable
        val backgroundDrawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.background_image)
        backgroundImageView.setImageDrawable(backgroundDrawable)

        // Настройка анимации для текста
        val scaleAnimation = ScaleAnimation(
            1.0f, 1.1f, 1.0f, 1.1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 1000
            repeatCount = Animation.INFINITE
            repeatMode = Animation.REVERSE
        }

        animatedTextView.startAnimation(scaleAnimation)

        startGameButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
