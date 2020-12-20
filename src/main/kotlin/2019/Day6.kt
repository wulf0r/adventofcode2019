import kotlin.math.max
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun main() {

    part1Test()
    part2Test()

    timed("total") {
        val realSpecs = timed("reading and parsing") {
            parse(readInput("day6.txt"))
        }
        val realTree = timed("buildingTree") {
            OrbitTree(realSpecs)
        }
        timed("counting orbits") {
            require(realTree.countNumberOfOrbits() == 171213)
        }
        timed("counting transfers") {
            require(realTree.countTransfers("YOU", "SAN") == 292)
        }
    }
}


@ExperimentalTime
fun part1Test() {

    val testData = """COM)B
        |B)C
        |C)D
        |D)E
        |E)F
        |B)G
        |G)H
        |D)I
        |E)J
        |J)K
        |K)L""".trimMargin("|")


    val testSpecs = parse(testData)

    require(testSpecs.size == 11)
    require(testSpecs.first().centralBody == "COM" && testSpecs.first().orbitingBody == "B")
    require(testSpecs.determineCentralBody() == "COM")

    val tree = OrbitTree(testSpecs)

    require(tree.find("COM")?.bodyName == "COM")

    val lookupResult = testSpecs.toUniqueBodies().mapNotNull{bodyName ->
        tree.find(bodyName)?.bodyName
    }.toSortedSet()
    require(lookupResult == testSpecs.toUniqueBodies())
    require(tree.countNumberOfNodes() == 12)
    require(tree.countNumberOfOrbits() == 42)

}
@ExperimentalTime
fun part2Test() {
    /*** part 2 **/
    val testData = """
    COM)B
    B)C
    C)D
    D)E
    E)F
    B)G
    G)H
    D)I
    E)J
    J)K
    K)L
    K)YOU
    I)SAN""".trimIndent()

    val testSpec = parse(testData)
    val testTree = OrbitTree(testSpec)
    require(testTree.shortestPath("YOU", "YOU") == emptyList<OrbitTree.OrbitNode>())
    require(testTree.countTransfers("YOU", "SAN") == 4)


}


@ExperimentalTime
class OrbitTree(specs : List<OrbitSpec>) {

    data class OrbitNode( val parent : OrbitNode?, val bodyName : String) {
        val children = mutableListOf<OrbitNode>()
        val pathToRoot : List<OrbitNode> by lazy {
            when(parent) {
                null -> emptyList()
                else -> listOf(parent) + parent.pathToRoot
            }
        }

        fun listDescendants(): List<OrbitNode> = children + children.map { it.listDescendants() }.flatten()
        val isLeaf
                get() = children.isEmpty()

        override fun toString(): String {
            return bodyName
        }

    }
    private val centralBodyToSpecs = specs.groupBy { it.centralBody }
    private val root : OrbitNode = OrbitNode(null, specs.determineCentralBody())
    private val nameToOrbitNode = mutableMapOf(root.bodyName to root)
    init {
        createNodes(root)
    }

    private fun createNodes(parent : OrbitNode) {
        centralBodyToSpecs[parent.bodyName]
            ?.map{ findOrCreate(parent, it.orbitingBody) }
            ?.forEach { createNodes(it) }
    }

    private fun findOrCreate(parent : OrbitNode, name : String) = nameToOrbitNode[name] ?: OrbitNode(parent, name).apply{
        parent.children += this
        nameToOrbitNode[name] = this
    }

    fun find(name : String) : OrbitNode? = nameToOrbitNode[name]

    override fun toString(): String {
        return toString(root, 0)
    }
    private fun toString(node : OrbitNode, indent : Int) : String {
        return node.bodyName.prependIndent(CharArray(indent) {' '}.joinToString("")) + "\n" + node.children.joinToString ("\n") { toString(it, indent + 4) }
    }

    fun countNumberOfNodes() = nameToOrbitNode.size

    fun countNumberOfOrbits() : Int = listAllNodes().sumBy { it.pathToRoot.size }
    fun listAllNodes() = nameToOrbitNode.values
    fun listAllLeaves() = listAllNodes().filter { it.isLeaf }

    fun shortestPath(bodyFrom : String, bodyTo : String) : List<OrbitNode> {
        val bodyFrom = find(bodyFrom) ?: error("$bodyFrom is not a valid node")
        val bodyTo = find(bodyTo) ?: error("$bodyTo is not a valid node")

        if( bodyFrom == bodyTo) return  emptyList()
        val fromRootPath = bodyFrom.pathToRoot
        val toRootPath = bodyTo.pathToRoot

        val intersectionNode = fromRootPath.intersect(toRootPath).first()

        return listOf(bodyFrom) + fromRootPath.subList(intersectionNode) + listOf(intersectionNode) + toRootPath.subList(intersectionNode) + listOf(bodyTo)
    }
    fun countTransfers(bodyFrom : String, bodyTo : String) = max(shortestPath(bodyFrom, bodyTo).size - 3,0)

    private inline fun List<OrbitNode>.subList(to : OrbitNode) = this.subListOrNull(to) ?: error("Node $to not found in $this")
    private inline fun List<OrbitNode>.subListOrNull(to : OrbitNode) = when(val idx = this.indexOf(to)) {
        -1 -> null
        else -> subList(0, idx)
    }

}

data class OrbitSpec(val spec : String) {
    val centralBody = spec.split(")")[0]
    val orbitingBody = spec.split(")")[1]

}

fun parse(specs : String) = parse(specs.lines())
fun parse(specs: List<String>) = specs.map { OrbitSpec(it) }

fun List<OrbitSpec>.toUniqueBodies() = (map { it.centralBody } + map { it.orbitingBody }).toSortedSet()
fun List<OrbitSpec>.determineCentralBody() = "COM"//this.map { it.centralBody }.toSet().singleOrNull { centralBody -> !this.map { it.orbitingBody }.contains(centralBody) } ?: error("could not find exactly one COM")
