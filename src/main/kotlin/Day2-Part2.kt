/** https://adventofcode.com/2019/day/2#part2 **/
/** Answer: 9342 **/
fun main() {

    val program = compile(readInput("day2.txt").first())

    val computer = ShipComputerV5()
    for(noun in 0L..99) {
        for( verb in 0L..99) {
            if( computer.compute(program, noun, verb,{50},{}) == 19690720L) {
                println("100 * noun $noun + verb $verb = ${noun * 100 + verb}" )
            }
        }

    }

}