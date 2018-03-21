import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

fun main(args:Array<String>){

    var solutionIndex = 1


    val input = Input.readInput()
    val solution = Solution(input).getSolution()

    var outputFile = File("./solution.txt")
    while(outputFile.exists()){
        outputFile = File("./solution$solutionIndex.txt")
        solutionIndex++
    }

    val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(outputFile), StandardCharsets.UTF_8))
    writer.write(solution)
    writer.close()
}