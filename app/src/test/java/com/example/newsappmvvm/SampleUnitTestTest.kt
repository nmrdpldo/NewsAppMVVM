package com.example.newsappmvvm

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SampleUnitTestTest{

    @Test
    fun `returns fibonacci 50th number in the sequence returns 12586269025`() {
        val result = SampleUnitTest.fib(50)
        assertThat(result).isEqualTo(12586269025)
    }

    @Test
    fun `returns fibonacci 10th number in the sequence returns 55`() {
        val result = SampleUnitTest.fib(10)
        assertThat(result).isEqualTo(55)
    }


    @Test
    fun `opening and closing parenthesis count does not match returns false`() {
        val result = SampleUnitTest.checkBraces("(nimrod))")
        assertThat(result).isFalse()
    }

    @Test
    fun `opening and closing parenthesis count matches returns true`() {
        val result = SampleUnitTest.checkBraces("(nimrod)")
        assertThat(result).isTrue()
    }

    @Test
    fun `the data starts with closing parenthesis returns false`() {
        val result = SampleUnitTest.checkBraces(")nimrod(")
        assertThat(result).isFalse()
    }


    @Test
    fun `empty password returns false` () {
        val result = SampleUnitTest.validateRegistrationInput(
            "Nimrod",
            "",
            "123")
                    assertThat(result).isFalse()
    }

    @Test
    fun `empty confirm password returns false` () {
        val result = SampleUnitTest.validateRegistrationInput(
            "Nimrod",
            "123",
            "")
                    assertThat(result).isFalse()
    }

    @Test
    fun `password and confirm password matches returns true` () {
        val result = SampleUnitTest.validateRegistrationInput(
            "Nimrod",
            "123nimrod",
            "123nimrod")
                    assertThat(result).isTrue()
    }

    @Test
    fun `password and confirm password does not match returns false` () {
        val result = SampleUnitTest.validateRegistrationInput(
            "Nimrod",
            "123nimrod",
            "1234nimrod")
        assertThat(result).isFalse()
    }

    @Test
    fun `empty password and confirm password textfield is empty returns false` () {
        val result = SampleUnitTest.validateRegistrationInput(
            "Nimrod",
            "",
            "")
                    assertThat(result).isFalse()
    }

    @Test
    fun `password contains less than 2 digits returns false` () {
        val result = SampleUnitTest.validateRegistrationInput(
            "Nimrod",
            "1abc",
            "1abc")
                    assertThat(result).isFalse()
    }

    @Test
    fun `username already exists returns false` () {
        val result = SampleUnitTest.validateRegistrationInput(
            "Anthony",
            "123abc",
            "123abc")
        assertThat(result).isFalse()
    }


}