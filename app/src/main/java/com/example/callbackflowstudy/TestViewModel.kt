package com.example.callbackflowstudy

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


//네트워크 요청 결과 -> callback 결과
interface NetworkResult {
    fun success()
    fun success(int : Int)

    fun fail()

}

class TestViewModel : ViewModel()  {

    private suspend fun requestNetworkData(resultCallback : NetworkResult) {
        repeat(5) {
            delay(500)
            println("requestNetworkData() - ${Thread.currentThread().name}")
            resultCallback.success(it)
        }
    }

    private fun releaseNetwork() {
        println("releaseNetwork() - Network released")
    }

    @ExperimentalCoroutinesApi
    fun getNetworkResultFlow() : Flow<String> = callbackFlow {
        val callbackImpl = object : NetworkResult {
            override fun success() {
                TODO("Not yet implemented")
            }

            override fun success(result : Int) {
                println("Network request success - $result - ${Thread.currentThread().name}}")
                trySend("SUCCESS")
            }

            override fun fail() {
                println("Network request fail - ${Thread.currentThread().name}")
                trySend("ERROR")
                // 실패시 channel 을 닫는다.
                close()
            }
        }

        requestNetworkData(callbackImpl)

        // coroutine scope 가 cancel 또는 close 될때 호출..

        awaitClose {
            println("Release request! - ${Thread.currentThread().name}")
            releaseNetwork()
        }
    }
}

//callback flow 는 MainActivity 가 destroy 되거나 명시적으로 close()로 flow 가 종료된다.
//callback flow 는 cold stream 이다.


//네트워크 요청
