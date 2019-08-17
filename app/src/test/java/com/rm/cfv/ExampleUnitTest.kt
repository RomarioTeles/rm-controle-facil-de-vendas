package com.rm.cfv

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import br.com.rm.cfv.CfvApplication
import br.com.rm.cfv.activities.BaseActivity
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun addition_isCorrect() {

        assertEquals(false, context == null)
    }
}
