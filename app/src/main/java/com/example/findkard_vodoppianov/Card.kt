data class Card(
    val id: Int,
    val frontDrawableId: Int,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false,
    var position: Int
)