import kotlin.time.ExperimentalTime
@ExperimentalTime
fun main() {
    Day9.Part1.run()
    Day9.Part2.run()


}

object Day9 {
    val day9Input = compile(readInput("day9.txt").first())
    object Part1{
        fun run() {
            val selfReplicating = compile("109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99")
            require(ShipComputerV5().compute(selfReplicating) == selfReplicating)
            require(ShipComputerV5().computeSingleValue(listOf(1102,34915192,34915192,7,4,7,99,0)) == 1_219_070_632_396_864)
            require(ShipComputerV5().computeSingleValue(listOf(104,1125899906842624,99)) == 1125899906842624L)
            println( "tests passed")

            require(ShipComputerV5().computeSingleValue(day9Input, 1) == 2775723069L)


        }
    }
    @ExperimentalTime
    object Part2{
        fun run() {
            timed("Coords") {
                require(ShipComputerV5().computeSingleValue(day9Input, 2) == 49115L)
            }
        }
    }


}