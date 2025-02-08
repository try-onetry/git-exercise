package Class5Call

class B {
    fun message(name: String): String {
        return "Hello, $name, I from Kotlin"
    }
}

fun main() {
    val a = A()
    val msg = a.message("Kotlin")
    println(msg)
}