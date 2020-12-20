package year2020

import readInput

class BagRules(lines : List<String>) {
    companion object {
        val ITEM_REGEX = """(\d) (\w*\s\w*) bags?""".toRegex()
    }
    data class Bag(val name : String, var canContain : List<Pair<Int,Bag>>) {
        override fun toString(): String {
            return "Bag(name='$name', canContain=${canContain.map { (_,bag) -> bag.name }})"
        }
        fun canContain(other : String) : Boolean = canContain.any {  (_,bag) -> bag.name == other } || canContain.any { (_,bag) -> bag.canContain(other) }

        fun countContainedBags() : Int = canContain.fold(0) { acc, pair ->
            acc + pair.first + (pair.first * pair.second.countContainedBags())
        }
    }
    private val listOfBags : List<Bag>
    init {
        listOfBags = lines.map { line -> line.split("bags contain") }
             .map { (bag, contains) ->
                   bag.trim() to contains.trim().trimEnd('.').split(",").mapNotNull {
                       if( it == "no other bags") null
                       else ITEM_REGEX.matchEntire(it.trim())?.let { matcher ->
                           matcher.groupValues[1].toInt() to matcher.groupValues[2]
                       } ?: error("$it doesnt match regex")
                    }
            }
            .map { (bag, contains) -> Bag(bag, mutableListOf()) to contains}
            .toMap()
            .let { mapOfBags ->
                mapOfBags.forEach { (bag, contains) ->
                    bag.canContain = contains.map { (count,containedBag) -> count to mapOfBags.keys.first { it.name == containedBag }}
                }
                mapOfBags.keys.toList()
            }

    }
    fun findBagsContaining(name: String) = listOfBags.filter { it.canContain(name) }
    fun countContained(name : String) = listOfBags.first { it.name == name }.countContainedBags()
}


fun main() {
    val testRules = BagRules(readInput("year2020/day7_text.txt"))
    require(4 == testRules.findBagsContaining("shiny gold").size)
    require(32 == testRules.countContained("shiny gold"))

    val day7BagRules = BagRules(readInput("year2020/day7.txt"))
    require(257 == day7BagRules.findBagsContaining("shiny gold").size)
    println(1038 == day7BagRules.countContained("shiny gold"))



}