package com.example.findkard_vodoppianov

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import java.io.File

data class Card(
    val id: Int,
    val frontDrawableId: Int,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false,
    var position: Int // новое поле для отслеживания положения карточки
)

class MainActivity : AppCompatActivity() {
    private lateinit var gridLayout: GridLayout
    private lateinit var newGameButton: Button
    private lateinit var saveGameButton: Button
    private lateinit var loadGameButton: Button
    private lateinit var aboutButton: Button

    private val cards = mutableListOf<Card>()
    private var firstSelectedCard: Card? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridLayout = findViewById(R.id.gridLayout)
        newGameButton = findViewById(R.id.newGameButton)
        saveGameButton = findViewById(R.id.saveGameButton)
        loadGameButton = findViewById(R.id.loadGameButton)
        aboutButton = findViewById(R.id.aboutButton)

        newGameButton.setOnClickListener { startNewGame() }
        saveGameButton.setOnClickListener { saveGame() }
        loadGameButton.setOnClickListener { loadGame() }
        aboutButton.setOnClickListener { showAboutDialog() }

        startNewGame()
    }

    private fun startNewGame() {
        cards.clear()
        gridLayout.removeAllViews()

        val images = listOf(
            R.drawable.apelsin, R.drawable.arbyz, R.drawable.apple, R.drawable.banan,
            R.drawable.dinu, R.drawable.grysha, R.drawable.kiwi, R.drawable.klybn
        )
        val pairedImages = (images + images).shuffled()

        for (i in pairedImages.indices) {
            val card = Card(id = i, frontDrawableId = pairedImages[i], position = i)
            cards.add(card)
            val button = createCardButton(card)
            gridLayout.addView(button)
        }
    }

    private fun createCardButton(card: Card): AppCompatButton {
        val button = AppCompatButton(this)
        button.layoutParams = GridLayout.LayoutParams().apply {
            width = 0
            height = 0
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1)
            rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(8, 8, 8, 8)
            this.width = (resources.displayMetrics.widthPixels / gridLayout.columnCount) - 40
            this.height = (resources.displayMetrics.widthPixels / gridLayout.columnCount) - 24
        }
        button.setBackgroundResource(R.drawable.card_back)
        button.setOnClickListener { onCardClicked(card, button) }
        return button
    }

    private fun onCardClicked(card: Card, button: AppCompatButton) {
        if (card.isMatched || card == firstSelectedCard) return

        if (!card.isFlipped) {
            card.isFlipped = true
            flipCard(card, button)
        }

        if (firstSelectedCard == null) {
            firstSelectedCard = card
        } else {
            checkForMatch(firstSelectedCard!!, card)
            firstSelectedCard = null
        }
    }

    private fun flipCard(card: Card, button: AppCompatButton) {
        val scale = resources.displayMetrics.density
        val cameraDistance = 8000 * scale
        button.cameraDistance = cameraDistance

        val flipOut = ObjectAnimator.ofFloat(button, "rotationY", 0f, 90f)
        flipOut.duration = 250
        flipOut.addListener(object : AnimatorListenerAdapter() {
           override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                updateCardAppearance(card, button)
                val flipIn = ObjectAnimator.ofFloat(button, "rotationY", -90f, 0f)
                flipIn.duration = 250
                flipIn.start()
            }
        })
        flipOut.start()
    }

    private fun updateCardAppearance(card: Card, button: AppCompatButton) {
        val drawable: Drawable? = if (card.isFlipped) {
            ContextCompat.getDrawable(this, card.frontDrawableId)
        } else {
            ContextCompat.getDrawable(this, R.drawable.card_back)
        }
        button.background = drawable
    }

    private fun checkForMatch(card1: Card, card2: Card) {
        if (card1.frontDrawableId == card2.frontDrawableId) {
            card1.isMatched = true
            card2.isMatched = true
            checkForWin()
        } else {
            gridLayout.postDelayed({
                card1.isFlipped = false
                card2.isFlipped = false
                flipCard(card1, gridLayout.getChildAt(card1.id) as AppCompatButton)
                flipCard(card2, gridLayout.getChildAt(card2.id) as AppCompatButton)
            }, 500)
        }
    }

    private fun checkForWin() {
        if (cards.all { it.isMatched }) {
            showWinDialog()
        }
    }

    private fun showWinDialog() {
        AlertDialog.Builder(this)
            .setTitle("Поздравляю!")
            .setMessage("Вы победили!!!")
            .setPositiveButton("Новая игра") { _, _ -> startNewGame() }
            .setCancelable(false)
            .show()
    }

    private fun saveGame() {
        val file = File(filesDir, "saved_game.txt")
        file.printWriter().use { out ->
            for (card in cards) {
                out.println("${card.id},${card.frontDrawableId},${card.isFlipped},${card.isMatched},${card.position}")
            }
        }
    }

    private fun loadGame() {
        val file = File(filesDir, "saved_game.txt")
        if (file.exists()) {
            val lines = file.readLines()
            cards.clear()
            gridLayout.removeAllViews()

            for (line in lines) {
                val parts = line.split(',')
                val id = parts[0].toInt()
                val frontDrawableId = parts[1].toInt()
                val isFlipped = parts[2].toBoolean()
                val isMatched = parts[3].toBoolean()
                val position = parts[4].toInt()
                val card = Card(id = id, frontDrawableId = frontDrawableId, isFlipped = isFlipped, isMatched = isMatched, position = position)
                cards.add(card)
            }

            cards.sortBy { it.position }

            for (card in cards) {
                val button = createCardButton(card)
                gridLayout.addView(button)
                updateCardAppearance(card, button)
            }
        }
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Об игре")
            .setMessage("Это игра 'Найди совпадения картинок'.")
            .setPositiveButton("OK", null)
            .show()
    }
}
