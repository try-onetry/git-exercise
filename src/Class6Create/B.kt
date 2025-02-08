package Class6Create

class B {

    fun createA(): A {
        val a = A()
        return a;
    }

    fun message() {
        println("I am class B")
    }
}

fun main() {
    val b = B()
    val a2 = b.createA()
    a2.message()
}
