package vn.hexagon.vietnhat.data.model.remote

data class ChatResponse(
    val `data`: ChatContent,
    val errorId: Int,
    val message: String
)