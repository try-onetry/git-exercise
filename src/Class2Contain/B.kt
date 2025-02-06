package Class2Contain

import Class2Contain.A

class B {
    val numb = 1
    val strb = "I am class B"
    val a = A()
}

fun main(){
    val b = B()
    val str = b.a.getStra()
    println(str)
}