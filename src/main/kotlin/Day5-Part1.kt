/**
 * Answer: 7286649
 */
fun main() {
    val program = readInput("day5.txt").first().split(",").map(String::toInt).toMutableList()

    val computer = ShipComputerV5()
    computer.compute(program, {1}, {
        println("Output $it")
    })
}