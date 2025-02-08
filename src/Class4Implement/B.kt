package Class4Implement

class B : A {
    override fun funa() {
        println("I am Kotlin class B, implements Java interface A")
    }
}

fun main() {
    val b: A = B()
    b.funa()
}