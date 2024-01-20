package com.example.newsappmvvm

object SampleUnitTest {
    /**
     * Returns the n-th fibonacci number
     * They are defined like this:
     * fib(0) = 0
     * fib(1) = 1
     * fib(n) = fib(n-2) + fib(n-1)
     */
    fun fib(n: Int): Long {
        if(n == 0 || n == 1) {
            return n.toLong()
        }
        var a = 0L
        var b = 1L
        var c = 1L
        (1..n-1).forEach { i ->
            c = a + b
            a = b
            b = c
        }
        return c
    }

    /**
     * Checks if the braces are set correctly
     * e.g. "(a * b))" should return false
     */
    fun checkBraces(string: String): Boolean {
        var counter = 0

        for (i in string.indices) {
            if(string[i] == '(' || string[i] == ')'){
                if(i == 0 && string[0] == ')'){
                    return false
                }
                if(string[i] == '('){
                    counter++
                }
                if(string[i] == ')'){
                    counter--
                }
            }
        }

        if(counter != 0){
            return false
        }

        return true

        //return string.count { it == '(' } == string.count { it == ')' }
    }

    val userList = arrayOf("Peter","Tony","Anthony")

    fun validateRegistrationInput(userName : String,
                                  password : String,
                                  confirmPassword : String) : Boolean {

        if(password.isEmpty() || confirmPassword.isEmpty()){
            return false
        }

        if(password.count { it.isDigit() } < 2){
            return false
        }

        if(password != confirmPassword){
            return false
        }

        if(userList.contains(userName)){
            return false
        }


        return true
    }
}