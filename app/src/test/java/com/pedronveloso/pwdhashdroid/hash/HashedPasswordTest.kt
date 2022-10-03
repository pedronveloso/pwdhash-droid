package com.pedronveloso.pwdhashdroid.hash

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pedronveloso.pwdhashdroid.hash.HashedPassword.Companion.create
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HashedPasswordTest {

    @Test
    fun testToString() {
        val hashedPassword = create(
            "my53cret#",
            "example.com"
        )
        Assert.assertEquals("Bu6aSm+Zcsf", hashedPassword.toString())
    }

    @Test
    fun testToStringWithNonAsciiChars() {
        val hashedPassword = create(
            "mü53crét#",
            "example.com"
        )
        Assert.assertEquals("r9qeSjv+lwJ", hashedPassword.toString())
    }

    @Test
    fun testToStringWithNonLatin1Chars() {
        val hashedPassword = create(
            "中文العربي",
            "example.com"
        )
        Assert.assertEquals("AwMz3+BdMT", hashedPassword.toString())
    }

    @Test
    fun testToStringWithoutNonAlphanumeric() {
        val hashedPassword = create(
            "my53cret",
            "example.com"
        )
        Assert.assertEquals("CIUD4SCSgh", hashedPassword.toString())
    }

    @Test
    fun testToStringWithShortSecret() {
        val hashedPassword = create(
            "ab",
            "example.com"
        )
        Assert.assertEquals("0IKv", hashedPassword.toString())
    }

    @Test
    fun testToStringWithShortestSecret() {
        val hashedPassword = create(
            "a",
            "example.com"
        )
        Assert.assertEquals("9FBo", hashedPassword.toString())
    }

    @Test
    fun testToStringWithLongSecret() {
        val hashedPassword = create(
            "abcdefghijklmnopqrstuvwxyz0123456789=",
            "example.com"
        )
        val result = hashedPassword.toString()

        // The original algorithm appends NULL bytes at the beginning.
        // Those bytes should not be part of the output.
        // "\0\0\0\0XO3u58jVa1nd+8qd08SDIQ"
        Assert.assertEquals("XO3u58jVa1nd+8qd08SDIQ", result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testToStringWithEmptySecret() {
        val hashedPassword = create(
            "",
            "example.com"
        )
        hashedPassword.toString()
    }
}
