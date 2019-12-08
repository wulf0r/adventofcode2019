
fun main() {
    Day8.Part1.calc()
    Day8.Part2.calc()

}
object Day8 {
    object Part1 {
        fun calc() {
            Image.fromStringData("123456789012", 3, 2).apply {
                require(layers.size == 2)
                require(layers[0] == listOf(1,2,3,4,5,6))
                require(layers[1] == listOf(7,8,9,0,1,2))
            }

            Image.fromStringData(readInput("day8.txt").first(), 25,6).apply {
                require(2016 == checksum())
            }
        }
    }

    object Part2 {
        private val expectedResult =
        """|█  █ ████  ██  ████ █  █ 
           |█  █    █ █  █    █ █  █ 
           |████   █  █      █  █  █ 
           |█  █  █   █     █   █  █ 
           |█  █ █    █  █ █    █  █ 
           |█  █ ████  ██  ████  ██  """.trimMargin().replace("\r","")

        fun calc() {
            Image.fromStringData("0222112222120000", 2,2).apply {
                require(cull == listOf(0,1,1,0))
            }
            Image.fromStringData(readInput("day8.txt").first(), 25,6).apply {
                require(toString() == expectedResult)
                println(toString())
            }

        }
    }

    data class Image(val data : List<Int>, val width : Int, val height : Int) {
        val BLACK =  0
        val WHITE = 1
        val TRANSPARENT = 2

        companion object {
            fun fromStringData(text : String, width : Int, height: Int) = Image(text.map { Character.getNumericValue(it) }, width, height)
        }
        val layers = data.chunked(width * height)

        fun checksum() = layers.minBy { layer -> layer.count { it == 0 } }!!.let { layer -> layer.count { it == 1 } * layer.count { it == 2 }}
        val cull by lazy {
            layers.fold(Array(width * height){-1}.asList()) {acc, layer ->
                acc.zip(layer) {a: Int, b: Int ->
                    if( b != TRANSPARENT && a == -1) b else a
                }
            }
        }

        override fun toString() : String{
            return cull.chunked(width).map { row ->
                row.map {
                    if( it == BLACK) ' ' else '\u2588'
                }.joinToString("")
            }.joinToString("\n")
        }
    }
}


