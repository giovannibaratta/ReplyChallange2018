package util

object Distance {

    fun manhattanDistance(startX: Int, startY: Int, endX: Int, endY: Int): Int =
            Math.abs(startX - endX) + Math.abs(startY - endY)

    fun manhattanDistance(startX: Double, startY: Double, endX: Double, endY: Double): Double =
            Math.abs(startX - endX) + Math.abs(startY - endY)

    fun manhattanDistance(startX: Long, startY: Long, endX: Long, endY: Long): Long =
            Math.abs(startX - endX) + Math.abs(startY - endY)

    fun manhattanDistance(startX: Float, startY: Float, endX: Float, endY: Float): Float =
            Math.abs(startX - endX) + Math.abs(startY - endY)

    fun euclideanDistance(startX: Int, startY: Int, endX: Int, endY: Int): Double =
            Math.sqrt(Math.pow(startX.toDouble() - endX, 2.0) + Math.pow(startY.toDouble() - endY, 2.0))

    fun euclideanDistance(startX: Double, startY: Long, endX: Long, endY: Long): Double =
            Math.sqrt(Math.pow(startX.toDouble() - endX, 2.0) + Math.pow(startY.toDouble() - endY, 2.0))

    fun euclideanDistance(startX: Float, startY: Long, endX: Long, endY: Long): Double =
            Math.sqrt(Math.pow(startX.toDouble() - endX, 2.0) + Math.pow(startY.toDouble() - endY, 2.0))

    fun euclideanDistance(startX: Long, startY: Long, endX: Long, endY: Long): Double =
            Math.sqrt(Math.pow(startX.toDouble() - endX, 2.0) + Math.pow(startY.toDouble() - endY, 2.0))

}
