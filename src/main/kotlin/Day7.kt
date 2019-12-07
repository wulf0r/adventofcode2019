import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.math.max
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun main() {
    Day7.Part1.calc()

    Day7.Part2.calc()
}
@ExperimentalTime
object Day7 {
    object Part1 {
        private val range = 0..4
        private val phaseSettingSequences by lazy {
            sequence {
                for (a in range) {
                    for (b in range) {
                        for (c in range) {
                            for (d in range) {
                                for (f in range) {
                                    yield(listOf(a, b, c, d, f))
                                }
                            }
                        }
                    }
                }
            }.toList().filter { it == it.distinct() }
        }

        fun findMaxThrusterSignal(intCodeProgram: List<Int>): Pair<Int, List<Int>> {
            val computer = ShipComputerV5(debug = false, printMemory = false)
            return phaseSettingSequences.map { phaseSettings ->
                var output = 0
                range.map { amplifier ->
                    output = computer.compute(intCodeProgram, phaseSettings[amplifier], output) ?: error("no output")
                }
                output to phaseSettings
            }.maxBy { it.first }!!
        }

        fun calc() {
            test(
                compile("3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0"),
                43210,
                listOf(4, 3, 2, 1, 0),
                ::findMaxThrusterSignal)
            test(
                compile("3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0"),
                54321,
                listOf(0, 1, 2, 3, 4),
                ::findMaxThrusterSignal
            )
            test(
                compile("3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0"),
                65210,
                listOf(1, 0, 4, 3, 2),
                ::findMaxThrusterSignal
            )
            timed("maxThruster Part1") {
                require(21860 == findMaxThrusterSignal(compile(readInput("day7.txt").first())).first)
            }
        }


    }


    object Part2 {
        private val range = 5..9
        private val phaseSettingSequences by lazy {
            sequence {
                for (a in range) {
                    for (b in range) {
                        for (c in range) {
                            for (d in range) {
                                for (f in range) {
                                    yield(listOf(a, b, c, d, f))
                                }
                            }
                        }
                    }
                }
            }.toList().filter { it == it.distinct() }
        }
        fun findMaxThrusterSignal(intCodeProgram: List<Int>): Pair<Int, List<Int>> {
            return phaseSettingSequences.map { phaseSettings ->
                runBlocking {
                    val range = 0..range.last - range.first
                    val receiveChannels = range.map { _ -> Channel<Int>(1) }
                    val sendChannels = range.map { amplifier ->
                        when (amplifier) {
                            4 -> receiveChannels[0]
                            else -> receiveChannels[amplifier + 1]
                        }
                    }
                    GlobalScope.launch {
                        range.forEach { amplifier ->  receiveChannels[amplifier].send(phaseSettings[amplifier]) }
                        receiveChannels[0].send(0)
                    }
                    range.map { amplifier ->
                        GlobalScope.async {
                            val receiveChannel = receiveChannels[amplifier]
                            val sendChannel = sendChannels[amplifier]
                            var output = 0
                            ShipComputerV5(debug = false, printMemory = false).compute(intCodeProgram,
                                input = { receiveChannel.receive() },
                                output = { write: Int ->
                                if (amplifier == 4) {
                                    output = write
                                }
                                sendChannel.send(write)
                            })
                            if( amplifier == 4) output to phaseSettings else null
                        }
                    }.awaitAll().filterNotNull().first()
                }
            }.maxBy { it.first }!!
        }
        fun calc() {
            test(
                compile("3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5"),
                139629729,
                listOf(9,8,7,6,5),
                ::findMaxThrusterSignal
            )

            test(
                compile("3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10"),
                18216,
                listOf(9,7,8,5,6),
                ::findMaxThrusterSignal
            )
            timed("maxThruster Part2") {
                require(2645740 == findMaxThrusterSignal(compile(readInput("day7.txt").first())).first)
            }
        }
    }
    fun test(intCodeProgram: List<Int>, expectedThrusterSignal: Int, expectedPhaseSettings: List<Int>, findMaxThrusterSignal : (List<Int>) -> Pair<Int, List<Int>>) {
        var (thrusterSignal, phaseSettings) = findMaxThrusterSignal(intCodeProgram)
        require(thrusterSignal == expectedThrusterSignal) { "Thruster Signal should be $expectedThrusterSignal but is $thrusterSignal" }
        require(phaseSettings == expectedPhaseSettings) { "Phase settings should be $expectedPhaseSettings but should be $phaseSettings" }
        println("test passed")
    }
}



