package Class3Extend

class C : A() {
    fun callSuperClassMethod() {
        funa()
    }
    fun func() {
        println("I am class C")
    }
}

fun main() {
    val c = C()
    c.funa()
    c.func()
    c.callSuperClassMethod()
}