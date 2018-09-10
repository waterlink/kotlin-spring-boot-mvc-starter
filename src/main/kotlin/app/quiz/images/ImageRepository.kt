package app.quiz.images

interface ImageRepository {
    fun upload(image: ByteArray): String
}
