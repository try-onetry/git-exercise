import Op.*
import kotlin.random.Random

enum class Op(val string: String) {
    ADD("+"), MINUS("-");
}

private fun assertCalc(expect: Int, resultString: String) {
    val result = Exercise2.calc(resultString)
    require(expect == result) {
        "error: $resultString should be $result but was $expect"
    }
}

fun main() {
    assertCalc(-1, "-1")
    val random = Random.Default
    for (i in 0..10000) {
        var result = random.nextInt(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
        val resultString = StringBuilder(result.toString())
        val chooseNum = random.nextInt(0, 10)
        for (j in 0 until chooseNum) {
            val operation = Op.entries.random(random)
            resultString.append(operation.string)
            val now = random.nextInt(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            resultString.append(now.toString())
            when (operation) {
                ADD -> result += now
                MINUS -> result -= now
            }
        }
        assertCalc(result, resultString.toString())
    }
}