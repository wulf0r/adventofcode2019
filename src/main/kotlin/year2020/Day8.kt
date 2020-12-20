package year2020

import readInput
import kotlin.math.absoluteValue

val testProgram = """nop +0
acc +1
jmp +4
acc +3
jmp -3
acc -99
acc +1
jmp -4
acc +6""".split("\n")


fun main() {
    var comp = Computer(Program.compile(testProgram), debug = false).runToFirstDuplicateInstruction()
    require(comp.acc == 5)

    comp = Computer(Program.compile(testProgram).part2Mod(), debug = true).runToFirstDuplicateInstruction()
    require(comp.acc == 8)
    require(!comp.hasNext())

    comp = Computer(Program.compile(readInput("year2020/day8.txt"))).runToFirstDuplicateInstruction()
    require(1766 == comp.acc)



    comp = Computer(Program.compile(readInput("year2020/day8.txt")).part2Mod(), debug = true).runToFirstDuplicateInstruction()
    require(!comp.hasNext())
    println(comp.acc)

}
fun Computer.run() : Computer {
    forEach { executeInstruction(it) }
    return this
}
fun Computer.runToFirstDuplicateInstruction() : Computer {
    forEach {
        if( !this.executedInstructions.contains(this.pc)) {
            executeInstruction(it)
        } else return this
    }
    return this
}

enum class Operation {
    acc,jmp,nop
}
data class Instruction(val op : Operation, val argument : Int) {
    override fun toString(): String {
        return if( argument >= 0) "$op +$argument"
        else "$op $argument"
    }
}
data class Program(val instructions : List<Instruction>) {
    companion object {
        fun compile(code : List<String>) : Program {
            return code
                .map { it.split(" ") }
                .map { (op, arg) -> Operation.valueOf(op) to arg.toInt() }
                .map { (op, arg) -> Instruction(op, arg) }
                .let { Program(it) }
        }
    }
    fun part2Mod() : Program {
        /** absolute disgusting brute force **/
        return this.instructions.filter { it.op == Operation.jmp }.mapNotNull { instruction ->
            instructions.toMutableList().let {
                it[instructions.indexOf(instruction)] = instruction.copy(op = Operation.nop)
                val prog = Program(it)
                val comp = Computer(prog).runToFirstDuplicateInstruction()
                if( !comp.hasNext()) {
                    prog
                } else null
            }
        }.firstOrNull() ?:
            this.instructions.filter { it.op == Operation.nop}.mapNotNull { instruction ->
            instructions.toMutableList().let {
                it[instructions.indexOf(instruction)] = instruction.copy(op = Operation.jmp)
                val prog = Program(it)
                val comp = Computer(prog).runToFirstDuplicateInstruction()
                if( !comp.hasNext()) {
                    prog
                } else null
            }
        }.firstOrNull() ?: error("no prog found")
    }
}
class Computer(val program: Program, val debug : Boolean = false) : Iterator<Instruction> {
    var pc = 0
    var acc = 0
    val executedInstructions = mutableListOf<Int>()

    override fun hasNext()= pc < program.instructions.size

    override fun next() = program.instructions[pc++]

    fun executeInstruction(instruction: Instruction) {
        if( debug ) println("$instruction $pc $acc")
        executedInstructions += pc
        when(instruction.op) {
            Operation.nop -> Unit
            Operation.acc -> acc += instruction.argument
            Operation.jmp -> {
                pc += instruction.argument - 1
                if( debug) println("jumping to pc $pc")
            }
        }
    }
}