fun main() {
    println("Hello World!")
    add(1,2)
    add3number(1,2,3)
    multiply(1)

    println(minus(1, 2))

}

fun add(a: Int, b: Int): Int {
    return a + b
}

fun add3number(a: Int, b: Int, c: Int): Int {
    return a + b + c
}


fun multiply(a: Int): Int {
    return a * 2
}
fun minus(a: Int, b: Int): Int {
    return a - b

}