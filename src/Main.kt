fun main() {
    println("Hello World!")
    add(1, 2)
    add3number(1, 2, 3)
    println(minus(1, 2))
}

fun add(a: Int, b: Int): Int {
    return a + b
}

fun add3number(a: Int, b: Int, c: Int): Int {
    return a + b + c
}

fun minus(a: Int, b: Int): Int {
    return a - b
}