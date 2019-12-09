import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * Answer: 15724522
 */
@ExperimentalTime
fun main() {

    require(shutUpAndCalculate("3,9,8,9,10,9,4,9,99,-1,8", 111) == 0L)
    require(shutUpAndCalculate("3,9,8,9,10,9,4,9,99,-1,8", 8) == 1L)

    val programText = readInput("day5.txt").first()

    val program = compile(programText)
    val dur = measureTime {
        val output = shutUpAndCalculate(program, 5)
        println(" Diagnostic: ${output}")
        require(output == 15724522L)
    }
    println(" took ${dur.inMilliseconds}")


}

fun shutUpAndCalculate(programText : String, input : Cell) = shutUpAndCalculate(compile(programText), input)

fun shutUpAndCalculate(program : Program, input : Cell) : Cell? {
    return ShipComputerV5(debug = false, printMemory = false).computeSingleValue(program,input)
}


