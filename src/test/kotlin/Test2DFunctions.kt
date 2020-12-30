import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Test2DFunctions {

    @Test
    fun testIsInside() {
        var subject = listOf(listOf<Int>())
        assertFalse(subject.validIndex(0,0))
        subject = listOf(listOf<Int>(0))
        assertTrue(subject.validIndex(0,0))
        assertFalse(subject.validIndex(-1,0))
        assertFalse(subject.validIndex(0,1))
    }

    @Test
    fun testAdjacentPoints() {
        var subject = listOf(listOf<Int>())
        assertTrue(subject.adjacentPoints(0,0).isEmpty())

        subject = listOf(listOf(0))
        assertTrue(subject.adjacentPoints(0,0).isEmpty())

        subject = listOf(listOf(0,1))
        assertEquals(listOf(Point(1, 0)), subject.adjacentPoints(0,0))

        subject = listOf(listOf(0,1,2), listOf(0,1,2), listOf(0,1,2))

        assertEquals(
            listOf(Point(x=0, y=0), Point(x=1, y=0), Point(x=2, y=0), Point(x=0, y=1), Point(x=2, y=1), Point(x=0, y=2), Point(x=1, y=2), Point(x=2, y=2)),
            subject.adjacentPoints(1,1)
        )
    }

    @Test
    fun testGet() {
        val subject = listOf(listOf(10,11,12), listOf(20,21,22), listOf(30,31,32))
        assertEquals(10, subject[0,0])
        assertEquals(32, subject[2,2])

    }



}