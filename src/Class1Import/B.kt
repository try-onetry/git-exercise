package Class1Import

class B {
    val numb = 1
    val strb = "I am class B"
}

fun main(){
    val str = A.getStra() // 调用了 A 类的静态方法不涉及 B 类的创建或方法调用，不会死循环
    println(str)
}