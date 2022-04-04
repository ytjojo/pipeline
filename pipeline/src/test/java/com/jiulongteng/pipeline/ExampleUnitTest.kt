package com.jiulongteng.pipeline

import com.jiulongteng.pipeline.debug.JvmPipeline
import com.jiulongteng.pipeline.graph.ConditionGraphNode
import com.jiulongteng.pipeline.graph.GraphNode
import org.junit.Assert.*
import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
//        assertEquals(4, 2 + 2)
        val jvmPipeline = JvmPipeline();
        jvmPipeline.init()
        jvmPipeline.start()


    }

    @Test
    fun testScheduledThreadPoolExecutor(){
        var a = 1 or 2 or 4
        println((a and 1).toString())

    }


}