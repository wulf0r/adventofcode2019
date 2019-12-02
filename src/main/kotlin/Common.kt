
fun readInput(name : String) = Thread.currentThread().contextClassLoader.getResourceAsStream(name)?.use {resourceStream ->
    resourceStream.reader().useLines {
        it.toList()
    }
} ?: error("Input '$name' not found")


typealias Memory = MutableList<Int>
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