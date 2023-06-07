package com.wavesciences.phonearray

import org.junit.Assert.*
import org.junit.Test
import com.wavesciences.phonearray.AddRecordings
import java.io.File

class AddRecordingsTest {
    @Test
    fun calculateAmplitude_returnsMaxAmplitude() {
        // Create an instance of AddRecordings
        val addRecordings = AddRecordings()

        // Create a sample buffer
        val buffer = shortArrayOf(100, 200, 150, 50, 300)

        // Call the calculateAmplitude function and assert the result
        val amplitude = addRecordings.calculateAmplitude(buffer, buffer.size)
        assertEquals(300f, amplitude)
    }






}