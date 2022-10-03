package com.pedronveloso.pwdhashdroid.hash

import com.pedronveloso.pwdhashdroid.hash.DomainExtractor.extractDomain
import junit.framework.TestCase.assertEquals
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DomainExtractorTests {

    lateinit var testSamples: HashMap<String, String>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        testSamples = HashMap()
        testSamples["example.com"] = "example.com"
        testSamples["http://example.com"] =
            "example.com"
        testSamples["http://example.com/aPath/test.html"] =
            "example.com"
        testSamples["http://www.example.com"] =
            "example.com"
        testSamples["https://www.example.com"] =
            "example.com"
        testSamples["http://www.example.com/aPath/test.html"] =
            "example.com"
        testSamples["http://login.test.example.com"] =
            "example.com"
        testSamples["http://example.co.uk"] =
            "example.co.uk"
        testSamples["http://login.example.co.uk"] =
            "example.co.uk"
        testSamples["https://login.example.co.uk/test.htm"] =
            "example.co.uk"
    }

    @Test
    @Throws(Exception::class)
    fun testExtractDomain() {
        testSamples.forEach { (k, v) ->
            assertEquals(v, extractDomain(k))
        }
    }

    @Test
    @Throws(Exception::class)
    fun testExtractDomainWithEmptyStringInput() {
        Assert.assertEquals("", extractDomain(""))
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testExtractDomainWithNullInput() {
        extractDomain(null)
    }
}
