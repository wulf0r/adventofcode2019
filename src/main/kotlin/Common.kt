import kotlinx.coroutines.*
import kotlin.math.max
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

fun readIntListInput(name: String) = readInput(name).mapNotNull { it.toIntOrNull() }


fun readInput(name : String) = Thread.currentThread().contextClassLoader.getResourceAsStream(name)?.use { resourceStream ->
    resourceStream.reader().useLines {
        it.toList()
    }
} ?: error("Input '$name' not found")


typealias Cell = Long
typealias Reference = Int
typealias ReadInput = suspend () -> Cell
typealias WriteOutput = suspend (Cell) -> Unit

typealias Program = List<Cell>

class Memory(private val data : MutableList<Cell>) : MutableList<Cell> by data {
    override fun set(index: Int, element: Cell): Cell {
        if( index >= data.size) {
            extendRam(index + 1 - data.size)
        }
        return data.set(index, element)
    }
    private fun extendRam(min : Int) {
        val size = max(min, data.size / 2)
        println("extending ram by $size")
        data.addAll(Array(size) {0L})
    }

}

class ShipComputerV5(var debug : Boolean = false, var printMemory : Boolean = false) {
    private var memory : Memory = Memory(mutableListOf())

    fun compute(program : Program, arg1 : Cell, arg2: Cell, input : ReadInput, output : WriteOutput) : Cell {
        memory = Memory(program.toMutableList())
        memory[1] = arg1
        memory[2] = arg2
        return computeImpl(input, output)
    }

    /**
     * Reads input from fn ReadInput
     * Writes output to fn WriteOutput
     * Returns memory[0] on termination
     */
    fun compute(program : Program, input : ReadInput, output : WriteOutput) : Cell {
        memory = Memory(program.toMutableList())
        return computeImpl(input, output)
    }

    /**
     * Feeding inputs from the supplied array in the given order
     * The first input instruction read will return inputs[0], the second read will return inputs[1] and so on
     *
     * Will return the last output written or null if the program contained no output instruction
     */
    fun computeSingleValue(program : Program, vararg inputs : Cell) : Cell? = compute(program, *inputs).lastOrNull()

    fun compute(program: Program, vararg inputs : Cell) : List<Cell> {
        val output = mutableListOf<Cell>()
        val inputs = inputs.iterator()
        compute(program, {
            if( !inputs.hasNext()) error("More reads than input values supplied Inputs: $inputs")
            inputs.next()
        } ) {
            output += it
        }
        return output
    }

    private val lt : (Cell, Cell) -> Cell = {val1, val2 -> if(val1 < val2) 1 else 0}
    private val eq : (Cell, Cell) -> Cell = {val1, val2 -> if(val1 == val2) 1 else 0}

    private val arithLookup = Array(9) {
        when(it) {
            1 -> Cell::plus
            2 -> Cell::times
            7 -> lt
            8 -> eq
            else -> { _,_ -> error("Invalid Op Code $it") }
        }
    }

    private val defaultModes = Array(3) {'0'}

    private fun computeImpl(input : ReadInput, output : WriteOutput) : Cell {
        var ip = 0L
        var relativeBase = 0
        loop@while(true) {
            if( printMemory) {
                memory.forEachIndexed { index, cell ->  println("[$index]=$cell")}
            }
            val cell = memory[ip.toInt()].toString().toCharArray()
            val modes = (defaultModes + cell.dropLast(2)).takeLast(3).reversed()

            val opcode = cell.takeLast(2).joinToString("").toInt()

            /** params are the parameters to the opcode **/

            val params = memory.subList(ip.toInt() + 1, min(memory.size, (ip + 4).toInt()))

            val storeAdr = when(opcode) {
                1,2,7,8 -> 2
                3 -> 0
                else -> null
            }?.let { idx ->
                when(val mode = modes[idx]) {
                    '0' -> params[idx]
                    '2' -> relativeBase + params[idx]
                    else -> error("Unsupported store adr parameter mode '$mode'")
                }
            }?.toInt() ?: Integer.MIN_VALUE

            /**
             * values are the resolved values from the parameters according to the mode
             * For simplicities sake always resolve all params to values
             **/
            val values = params.mapIndexed {  idx, param ->
                when(val mode = modes[idx]) {
                    '0' -> memory.getOrElse(param.toInt()) { 0 }
                    '1' -> param
                    '2' -> memory.getOrElse(relativeBase + param.toInt()) { 0 }
                    else -> error("Unsupported value parameter mode '$mode'")

                }
            }
            if( debug) println("ip $ip opcode $opcode (${cell.joinToString("")}) params $params values $values modes: $modes")
            when(opcode) {
                99 -> break@loop
                1,2,7,8 -> {
                    memory[storeAdr] = arithLookup[opcode](values[0], values[1])
                    ip += 4
                }
                3 -> {
                    memory[storeAdr] = runBlocking {
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
                5 -> if(values[0] != 0L) ip = values[1] else ip += 3
                6 -> if(values[0] == 0L) ip = values[1] else ip += 3
                9 -> {
                    relativeBase += values[0].toInt()
                    ip += 2
                }
                else -> error("unkown opcode $opcode")
            }
        }
        return memory[0]
    }
}

fun progression(from : Int, to : Int) : IntProgression {
    return if( from < to) from .. to else from downTo to
}

data class Point( val x : Int, val y : Int) : Comparable<Point> {
    fun offsetX(xOffset : Int) = Point(x + xOffset, y)
    fun offsetY(yOffset : Int) = Point(x, yOffset + y)

    fun slope(other : Point) =  (other.y - y).toDouble() / (other.x - x).toDouble()

    operator fun minus(other : Point) = Point(x - other.x, y - other.y)

    override operator fun compareTo(other : Point) = when( val difference = x - other.x) {
        0 -> y - other.y
        else -> difference
    }
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

fun compile(programText: String) : Program = programText.split(",").map(String::toLong).toList()

/**
 * Thanks @ https://www.reddit.com/user/spweller/
 */
fun <T> permutations(items: Set<T>): List<List<T>> {
    if (items.size == 1) {
        return listOf(listOf(items.first()))
    }

    return items.map { currentItem ->
        permutations(items.minus(currentItem)).map { it.plus(currentItem) }.flatten()
    }
}

fun <T> Iterable<T>.permutations() : List<List<T>> = permutations(this.toSet())

operator fun <ITEM_TYPE_FIRST, ITEM_TYPE_SECOND> Iterable<ITEM_TYPE_FIRST>.times(other : Iterable<ITEM_TYPE_SECOND>) : Sequence<Pair<ITEM_TYPE_FIRST, ITEM_TYPE_SECOND>> {
    val one = this
    return sequence<Pair<ITEM_TYPE_FIRST,ITEM_TYPE_SECOND>> {
        one.forEach { oneItem ->
            other.forEach { twoItem ->
                yield(oneItem to twoItem)
            }
        }
    }
}


fun <ITEM_TYPE_FIRST, ITEM_TYPE_SECOND,ITEM_TYPE_THIRD> Iterable<ITEM_TYPE_FIRST>.crossProduct(two : Iterable<ITEM_TYPE_SECOND>, third : Iterable<ITEM_TYPE_THIRD>) : Sequence<Triple<ITEM_TYPE_FIRST, ITEM_TYPE_SECOND, ITEM_TYPE_THIRD>> {
    val one = this
    return sequence<Triple<ITEM_TYPE_FIRST,ITEM_TYPE_SECOND,ITEM_TYPE_THIRD>> {
        one.forEach { oneItem ->
            two.forEach { twoItem ->
                third.forEach { thirdItem ->
                    yield(Triple(oneItem,twoItem,thirdItem))
                }

            }
        }
    }
}

fun <OUT_TYPE:Any> Boolean.map(ifTrue : OUT_TYPE, ifFalse : OUT_TYPE) = if (this) ifTrue else ifFalse

fun List<String>.groupByBlankLine() : List<List<String>> {
    return (this.mapIndexedNotNull { index, line ->
        if (line.isBlank()) index else null
    } + this.size).fold(0 to mutableListOf<List<String>>()) { (start, list), end ->
        list.add(this.subList(start, end))
        end to list
    }.second
}
fun List<String>.filterNotBlank() = this.filterNot { it.isBlank() }

fun Int.factorial() : Long {
    var fact = 1L
    for( i in 2..this) {
        fact *= i
    }
    return fact;
}

/**
 * Map a 2D list to a different 2D list
 */
inline fun <OUT_TYPE : Any, IN_TYPE : Any> List<List<IN_TYPE>>.mapIndexed2d(crossinline block : (x : Int, y : Int, value : IN_TYPE) ->OUT_TYPE) : List<List<OUT_TYPE>> {
    val list = this
    return list.mapIndexed { y, cols -> cols.mapIndexed { x, value -> block(x, y, value) } }
}

inline fun <OUT_TYPE : Any, IN_TYPE : Any> List<List<IN_TYPE>>.pmapIndexed2d(crossinline block : (x : Int, y : Int, value : IN_TYPE) ->OUT_TYPE) : List<List<OUT_TYPE>> {
    val list = this
    return runBlocking(Dispatchers.Default) {
        list.mapIndexed { y, cols -> cols.mapIndexed { x, value -> async { block(x, y, value) } } }.map { it.awaitAll() }
    }
}

/**
 * Are x,y a valid index for this 2D list? Y is rows in the outer list and x refers to items in the inner lists.
 */
inline fun <ELEMENT_TYPE : Any> Matrix<ELEMENT_TYPE>.validIndex(x : Int, y : Int) : Boolean{
    if( y < 0 || x < 0) return false
    if(this.isEmpty()) return false
    if( y >= this.size) return false
    if(x >= this[y].size) return false
    return true
}

/**
 * Are the x and y of the point a valid index for this 2D list.
 */
inline fun <IN_TYPE : Any> List<List<IN_TYPE>>.validIndex(point: Point) = this.validIndex(point.x, point.y)

/**
 * Convert a x,y pair of ints to a point
 */
inline fun Pair<Int, Int>.toPoint() = Point(first, second)

/**
 * List all adjacent points to (x,y) such that adjacent point have coordinates x-1, x, x+1 and y-1, y, y+1.
 * Only returns points that are valid indices for this list.
 * Does not return the point of the initial coordinate.
 */
fun <IN_TYPE : Any> List<List<IN_TYPE>>.adjacentPoints(x : Int, y : Int) : List<Pair<Int, Int>> {
    return axes.map { it(x,y) }.filter { (x, y) -> this.validIndex(x, y) }.toList()
}

fun <ITEM_TYPE : Any> List<List<ITEM_TYPE>>.adjacentPoints(point : Point) = this.adjacentPoints(point.x, point.y)

/**
 * Get the element at the index x index of the item, y index of the list
 */
operator fun <ITEM_TYPE : Any> List<List<ITEM_TYPE>>.get(x : Int, y : Int) : ITEM_TYPE {
    return this[y][x]
}

fun <ITEM_TYPE : Any> List<List<ITEM_TYPE>>.getOrNull(x : Int, y : Int) : ITEM_TYPE? {
    return this.getOrNull(y)?.getOrNull(x)
}

operator fun <ITEM_TYPE : Any> List<List<ITEM_TYPE>>.get(point: Point) = this[point.x, point.y]

fun <ITEM_TYPE : Any> List<List<ITEM_TYPE>>.getOrNull(point: Point) = this.getOrNull(point.x, point.y)

fun <ITEM_TYPE : Any> List<List<ITEM_TYPE>>.getAll(indices : List<Pair<Int,Int>>) =
    indices.map { (x,y) -> this[x,y] }

/**
 * Yields the value at index x,y in *list* for each pair in this
 */
fun <ITEM_TYPE : Any> List<Pair<Int,Int>>.valuesForIndices(list : List<List<ITEM_TYPE>>) : List<ITEM_TYPE> {
    return list.getAll(this)
}

typealias Axis = (Int, Int) -> Pair<Int,Int>

val axes = sequenceOf<Axis>(
    {x,y -> x - 1 to y - 1},
    {x,y -> x     to y - 1},
    {x,y -> x + 1 to y - 1},
    {x,y -> x - 1 to y    },
    {x,y -> x + 1 to y    },
    {x,y -> x - 1 to y + 1},
    {x,y -> x     to y + 1},
    {x,y -> x + 1 to y + 1}
)

typealias Matrix<ELEMENT_TYPE> = List<List<ELEMENT_TYPE>>
fun <ELEMENT_TYPE : Any> Matrix<ELEMENT_TYPE>.pointsOnAxes(originX : Int, originY : Int) : Sequence<Pair<Axis, Sequence<Pair<Int,Int>>>> {
    val matrix = this
    return axes.map {
        it to it.pointsFor(originX, originY).takeWhile { (x,y) -> matrix.validIndex(x,y) }
    }
}


fun Axis.pointsFor(originX : Int, originY : Int) : Sequence<Pair<Int, Int>> {
    val axis = this
    return sequence {
        var pair = Pair(originX, originY)
        while( true ) {
            pair = axis(pair.first, pair.second)
            yield(pair)
        }
    }
}
