class BugKotlin {
    private val javaClass = BugStr()
    val str: String = javaClass.getStr()
}

fun main() {
    val javaClass = BugStr()
    try {
        // 调用 Java 域
        val a: String = javaClass.a
        println("a:$a")
    } catch (e: NullPointerException) {
        println("NullPointerException caught: ${e.message}")
    } catch (e: Exception) {
        println("Exception caught: ${e.message}")
    }
    try {
        // 用属性访问 Java 的 getter
        val b = BugKotlin()
        println("b:${b.str}")
    } catch (e: NullPointerException) {
        println("NullPointerException caught: ${e.message}")
    } catch (e: Exception) {
        println("Exception caught: ${e.message}")
    }
    try {
        // 调用 Java 函数
        val result: String = javaClass.result()
        println("result:$result")
    } catch (e: NullPointerException) {
        println("NullPointerException caught: ${e.message}")
    } catch (e: Exception) {
        println("Exception caught: ${e.message}")
    }
}
