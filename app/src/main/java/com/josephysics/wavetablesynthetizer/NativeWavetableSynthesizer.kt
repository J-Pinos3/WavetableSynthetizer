@file:OptIn(InternalCoroutinesApi::class)

package com.josephysics.wavetablesynthetizer

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.withContext

class NativeWavetableSynthesizer: WavetableSynthesizer, DefaultLifecycleObserver {

    private var synthesizerHandle: Long = 0L
    private val synthesizerMutex = Object()
    private external fun create(): Long
    private external fun delete(synthesizerHandle: Long)
    private external fun play(synthesizerHandle: Long)
    private external fun stop(synthesizerHandle: Long)
    private external fun isPlaying(synthesizerHandle: Long): Boolean
    private external fun setFrequency(synthesizerHandle: Long, frequencyInHz :Float)
    private external fun setVolume(synthesizerHandle: Long, volumeInDb: Float)
    private external fun setWavetable(synthesizerHandle: Long, wavetable: Int)

    /**a native library is a shared library, in this case, we load it statically*/
    companion object{
        init {
            System.loadLibrary("wavetablesynthesizer")
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        synchronized(synthesizerMutex){
            createNativeHandleIfNotExists()//if synthesizerHandle is 0, create it
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)

        synchronized(synthesizerMutex){
            if(synthesizerHandle == 0L){
                //dont destroy it twice
                return
            }

            delete(synthesizerHandle)
            synthesizerHandle = 0L
        }
    }


    private fun createNativeHandleIfNotExists() {
        if(synthesizerHandle != 0L){
            return
        }
        synthesizerHandle = create()
    }


    @OptIn(InternalCoroutinesApi::class)
    override suspend fun play() = withContext(Dispatchers.Default) {
        synchronized(synthesizerMutex){
            createNativeHandleIfNotExists()
            play(synthesizerHandle)
        }
    }

    override suspend fun stop() = withContext(Dispatchers.Default) {
        synchronized(synthesizerMutex){
            createNativeHandleIfNotExists()
            stop(synthesizerHandle)
        }
    }

    override suspend fun isPlaying(): Boolean = withContext(Dispatchers.Default) {
        synchronized(synthesizerMutex){
            createNativeHandleIfNotExists()
            return@withContext isPlaying(synthesizerHandle)
        }
    }

    override suspend fun setFrequency(frequencyInHz: Float) = withContext(Dispatchers.Default) {
        synchronized(synthesizerMutex){
            createNativeHandleIfNotExists()
            setFrequency(synthesizerHandle, frequencyInHz)
        }
    }

    override suspend fun setVolume(volumeInDb: Float) = withContext(Dispatchers.Default){
        synchronized(synthesizerMutex){
            createNativeHandleIfNotExists()
            setVolume(synthesizerHandle, volumeInDb)
        }
    }

    override suspend fun setWavetable(wavetableSynthesizer: Wavetable) {
        synchronized(synthesizerMutex){
            createNativeHandleIfNotExists()
            setWavetable(synthesizerHandle, wavetableSynthesizer.ordinal)
        }
    }

}