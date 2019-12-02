/** https://adventofcode.com/2019/day/2#part2 **/
/** Answer: 9342 **/
fun main() {

    val program = readInput("day2.txt").first().split(",").map(String::toInt).toMutableList()

    val computer = ShipComputer()
    for(noun in 0..99) {
        for( verb in 0..99) {
            if( computer.compute(program, noun, verb) == 19690720) {
                println("100 * noun $noun + verb $verb = ${noun * 100 + verb}" )
            }
        }

    }

}