package com.example.ganvas3

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.model.Model
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ImageStyleTransferHelper(context: Context) {
    private val generatorModel: Interpreter

    init {
        val generatorModelBuffer = loadModelFile(context, "ml/monet_generator_model.tflite")
        generatorModel = Interpreter(generatorModelBuffer, Interpreter.Options().apply {
            setNumThreads(4)
        })
    }

    private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        val inputStream = fileDescriptor.createInputStream()
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun transformImage(bitmap: Bitmap): Bitmap {
        val inputImage = TensorImage.fromBitmap(bitmap)
        val outputImage = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val outputTensorImage = TensorImage.fromBitmap(outputImage)

        generatorModel.run(inputImage.buffer, outputTensorImage.buffer)

        return outputTensorImage.bitmap
    }
}