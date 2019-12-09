/**
 * Answer: 7286649
 */
fun main() {
    val program = compile(readInput("day5.txt").first())

    val computer = ShipComputerV5()
    computer.compute(program, {1}, {
        println("Output $it")
    })
}