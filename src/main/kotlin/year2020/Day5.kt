package year2020

import readInput
import java.lang.StringBuilder
import kotlin.Comparator
import kotlin.math.roundToInt

class Bsp<ITEMTYPE:Any>(private val item : ITEMTYPE, items : List<ITEMTYPE>, private val fnPlane : (ITEMTYPE, ITEMTYPE) -> Boolean) {

    val up : Bsp<ITEMTYPE>?
    val down : Bsp<ITEMTYPE>?

    init {
        val (front, back) = items.partition(item)
        up = (front - item).firstOrNull()?.let { Bsp(it, front - item , fnPlane) }
        down = (back - item).firstOrNull()?.let { Bsp(it, back - item, fnPlane) }

    }
    private fun List<ITEMTYPE>.partition(root : ITEMTYPE) : Pair<List<ITEMTYPE>, List<ITEMTYPE>>{
        return this.groupBy { fnPlane(root, it) }.let { it.getOrDefault(true, emptyList()) to it.getOrDefault(false, emptyList()) }

    }

    override fun toString(): String {
        return toStringImpl("")
    }
    private fun toStringImpl(indent : String) : String {
        val builder = StringBuilder("${indent}$item")
        if( up != null) {
            builder.append("\n${indent}Up:\n")
            builder.append(up.toStringImpl("$indent  "))
        }
        if( down != null) {
            builder.append("\n${indent}Down:\n")
            builder.append(down.toStringImpl("$indent  "))
        }
        return builder.toString()
    }
}

class BinaryTree<ITEM_TYPE : Comparable<ITEM_TYPE>>(_items : List<ITEM_TYPE>) {
    private val items = _items.sorted()

    data class Node<ITEM_TYPE : Comparable<ITEM_TYPE>>(val item : ITEM_TYPE, val _left : List<ITEM_TYPE>, val _right : List<ITEM_TYPE>, val depth : Int) {

        val left : Node<ITEM_TYPE>? = _left.pivot()?.let { Node(it.pivot, it.left, it.right, depth+1) }
        val right : Node<ITEM_TYPE>? = _right.pivot()?.let { Node(it.pivot, it.left, it.right, depth+1) }

        val allToTheLeft: Sequence<Node<ITEM_TYPE>> get() = sequence { left?.let { left -> yield(left); yieldAll(left.allToTheLeft)  } }
        val allToTheRight: Sequence<Node<ITEM_TYPE>> get() = sequence { right?.let { right -> yield(right); yieldAll(right.allToTheRight)  } }

        val all : Sequence<Node<ITEM_TYPE>> get() = sequence {
            yieldAll(allToTheLeft)
            yieldAll(allToTheRight)
            listOfNotNull(left, right).forEach { yieldAll(it.all) }

        }

        fun child(leftSide : Boolean) = if(leftSide) left else right
    }

    val root : Node<ITEM_TYPE>
    init {
        root = items.pivot()?.let { pivot ->
            Node(pivot.pivot, pivot.left, pivot.right, 0)
        } ?: error("No elements in $_items")
    }
}



data class Pivot<ITEM_TYPE : Comparable<ITEM_TYPE>>(val left : List<ITEM_TYPE>, val pivot : ITEM_TYPE, val right : List<ITEM_TYPE>)
fun <ITEM_TYPE : Comparable<ITEM_TYPE>> List<ITEM_TYPE>.pivot(sort : Boolean = false, comparator: Comparator<ITEM_TYPE> = Comparator{one, two -> one.compareTo(two) }) : Pivot<ITEM_TYPE>? {
    val list = if( sort ) this.sortedWith(comparator) else this
    return when(list.size) {
        0 -> null
        1 -> Pivot(emptyList(),list[0], emptyList())
        else -> {
            val pivotIndex = (list.size / 2.0).roundToInt() - 1
            Pivot(list.subList(0, pivotIndex + 1), list[pivotIndex], list.subList(pivotIndex + 1, list.size))
        }
    }
}


fun main() {
    require(357 == BoardingPass("FBFBBFFRLR").seatId)
    require(567 == BoardingPass("BFFFBBFRRR").seatId)
    require(119 == BoardingPass("FFFBBBFRRR").seatId)
    require(820 == BoardingPass("BBFFBBFRLL").seatId)

    val boardingPasses = readInput("year2020/day5.txt")
        .filter { it.isNotBlank() }
        .map { line -> BoardingPass(line) }

    val maxSeatId = boardingPasses.maxOf { it.seatId }
    require(911 == maxSeatId)

    val (row, seats) = boardingPasses
        .map { it.row to it.seat }
        .groupBy { it.first }
        .filter { (row, seats) -> seats.size != 8 }
        .entries.first()

    require(row == 78)
    require(seats.none { (_, seat) -> seat == 5 })
    require(78 * 8 + 5 == 629)






}

class BoardingPass(boardingPassString : String) {
    val rowAddress = boardingPassString.subSequence(0,7)
    val seatAddress = boardingPassString.subSequence(7,10)

    companion object {
        val rows = BinaryTree((0..127).toList())
        val seats =  BinaryTree((0..7).toList())
    }

    val row = rows.navigate(rowAddress, 'F', 'B')
    val seat = seats.navigate(seatAddress, 'L', 'R')

    val seatId = row * 8 + seat

    override fun toString(): String {
        return "row: $row seat: $seat seatId: $seatId"
    }
}

fun <ITEM_TYPE : Comparable<ITEM_TYPE>> BinaryTree<ITEM_TYPE>.navigate(navigate : CharSequence, leftChar : Char, rightChar : Char) : ITEM_TYPE {
    return navigate.map { it == leftChar }.fold(root) { node, leftSide ->
        node.child(leftSide) ?: error("No node for $navigate last node $node")
    }.item
}

