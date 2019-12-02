/**
 *  https://adventofcode.com/2019/day/2#part1
 *  Answer: 2894520
*/

fun main() {

    val memory = readInput("day2.txt").first().split(",").map(String::toInt).toMutableList()

    memory[1] = 12
    memory[2] = 2
    println(memory)
    val instructions = memory.chunked(4)
    try {
        instructions.forEach { instruction ->
            when (val opcode = instruction.first()) {
                99 -> return@main
                else -> {
                    val (load1, load2, store) = instruction.subList(1, instruction.size)
                    when (opcode) {
                        1 -> memory[store] = memory[load1] + memory[load2]
                        2 -> memory[store] = memory[load1] * memory[load2]
                    }
                }
            }
        }
    } finally {
        println(memory)
    }
}


