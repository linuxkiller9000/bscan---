package com.bscan.app.processing

import org.junit.Assert.assertEquals
import org.junit.Test

class ImageInspectorTest {

    @Test
    fun calculateInSampleSize_returnsOneForSmallImage() {
        val sample = ImageInspector.calculateInSampleSize(
            rawWidth = 800,
            rawHeight = 1200,
            maxLongEdgePx = 1600,
        )
        assertEquals(1, sample)
    }

    @Test
    fun calculateInSampleSize_downscalesHighResolutionPhotoByPowerOfTwo() {
        val sample = ImageInspector.calculateInSampleSize(
            rawWidth = 4000,
            rawHeight = 3000,
            maxLongEdgePx = 1600,
        )
        assertEquals(2, sample)

        val highResSample = ImageInspector.calculateInSampleSize(
            rawWidth = 8000,
            rawHeight = 6000,
            maxLongEdgePx = 1600,
        )
        assertEquals(4, highResSample)
    }

    @Test
    fun calculateInSampleSize_handlesInvalidDimensionsSafely() {
        assertEquals(1, ImageInspector.calculateInSampleSize(0, 0, 1600))
        assertEquals(1, ImageInspector.calculateInSampleSize(-10, 500, 1600))
    }
}
