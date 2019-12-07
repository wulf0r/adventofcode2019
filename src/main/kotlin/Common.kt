import kotlinx.coroutines.runBlocking
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

fun readInput(name : String) = Thread.currentThread().contextClassLoader.getResourceAsStream(name)?.use { resourceStream ->
    resourceStream.reader().useLines {
        it.toList()
    }
} ?: error("Input '$name' not found")


typealias Memory = MutableList<Int>
typealias Program = List<Int>
class ShipComputer {
    val memory : Memory = mutableListOf()

    fun compute(initialMemory : Memory, arg1 : Int, arg2: Int) : Int {
        memory.clear()
        memory.addAll(initialMemory)
        memory[1] = arg1
        memory[2] = arg2
        val instructions = initialMemory.chunked(4)
        instructions.forEach { instruction ->
            when (val opcode = instruction.first()) {
                99 -> return memory[0]
                else -> {
                    val (load1, load2, store) = instruction.subList(1, instruction.size)
                    when (opcode) {
                        1 -> memory[store] = memory[load1] + memory[load2]
                        2 -> memory[store] = memory[load1] * memory[load2]
                    }
                }
            }
        }
        return memory[0]
    }
}

typealias ReadInput = suspend () -> Int
typealias WriteOutput = suspend (Int) -> Unit

class ShipComputerV5(var debug : Boolean = true, var printMemory : Boolean = false) {
    private var memory : Memory = mutableListOf()

    fun compute(program : Program, arg1 : Int, arg2: Int, input : ReadInput, output : WriteOutput) : Int {
        memory = program.toMutableList()
        memory[1] = arg1
        memory[2] = arg2
        return computeImpl(input, output)
    }

    fun compute(program : Program, input : ReadInput, output : WriteOutput) : Int {
        memory = program.toMutableList()

        return computeImpl(input, output)
    }

    /**
     * Feeding inputs from the supplied array in the given order
     * The first input instruction read will return inputs[0], the second read will return inputs[1] and so on
     *
     * Will return the last output written or null if the program contained no output instruction
     */
    fun compute(program : Program, vararg inputs : Int) : Int? {
        var output : Int? = null
        val inputs = inputs.iterator()
        compute(program, {
            if( !inputs.hasNext()) error("More reads than input values supplied Inputs: $inputs")
            inputs.next()
        } ) {
            output = it
        }
        return output
    }

    private val lt : (Int, Int) -> Int = {val1, val2 -> if(val1 < val2) 1 else 0}
    private val eq : (Int, Int) -> Int = {val1, val2 -> if(val1 == val2) 1 else 0}

    private val arithLookup = Array(9) {
        when(it) {
            1 -> Int::plus
            2 -> Int::times
            7 -> lt
            8 -> eq
            else -> { _,_ -> error("Invalid Op Code $it") }
        }
    }

    private val defaultModes = Array(3) {'0'}

    private fun computeImpl(input : ReadInput, output : WriteOutput) : Int {
        var ip = 0
        loop@while(true) {
            if( printMemory) {
                memory.forEachIndexed { index, cell ->  println("[$index]=$cell")}
            }
            val cell = memory[ip].toString().toCharArray()
            val modes = (defaultModes + cell.dropLast(2)).takeLast(3).reversed()

            /** params are the parameters to the opcode **/
            val params = memory.subList(ip, min(memory.size, ip + 4)).drop(1)
            val storeAdr = params.lastOrNull() ?: Int.MIN_VALUE

            /**
             * values are the resolved values from the parameters according to the mode
             * For simplicities sake always resolve all params to values
             **/
            val values = params.mapIndexed {  idx, param ->
                if (modes[idx] == '0') memory.getOrElse(param) {Integer.MIN_VALUE} else param
            }

            when(val opcode = cell.takeLast(2).joinToString("").toInt().apply {
                if( debug) println("ip $ip opcode $this (${cell.joinToString("")}) params $params values $values modes: $modes")
            }) {
                99 -> break@loop
                1,2,7,8 -> {
                    memory[storeAdr] = arithLookup[opcode](values[0], values[1])
                    ip += 4
                }
                3 -> {
                    memory[params[0]] = runBlocking {
                        input()
                    }
                    ip += 2
                }
                4 -> {
                    runBlocking {
                        output(values[0])
                    }

                    ip += 2
                }
                5 -> if(values[0] != 0) ip = values[1] else ip += 3
                6 -> if(values[0] == 0) ip = values[1] else ip += 3
                else -> error("unkown opcode $opcode")
            }
        }
        return memory[0]
    }
}

fun progression(from : Int, to : Int) : IntProgression {
    return if( from < to) from .. to else from downTo to
}

data class Point( val x : Int, val y : Int) {
    fun offsetX(xOffset : Int) = Point(x + xOffset, y)
    fun offsetY(yOffset : Int) = Point(x, yOffset + y)
}


data class WireMovement(val direction : Char, val distance : Int)


@ExperimentalTime
fun <T> timed(desc: String, block : () -> T) : T {
    return measureTimedValue {
        block()
    }.let {
        println("$desc took ${it.duration.inMilliseconds} ms")
        it.value
    }
}

fun compile(programText: String) : Program = programText.split(",").map(String::toInt).toList()