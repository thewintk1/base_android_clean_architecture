package com.example.base_clean_architecture.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date


class UploadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    companion object {
        const val KEY_WORKER = "key_worker"
    }

    override suspend fun doWork(): Result {
        try {
            val count: Int = inputData.getInt("KEY_COUNT_VALUE", 0)
            Timber.tag("###BaseProject").d("KEY_COUNT_VALUE: $count")
            fetchDataFromNetwork(count)
            val time = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val curentDate = time.format(Date())
            val outPutDate = Data.Builder().putString(KEY_WORKER, curentDate).build()
            return Result.success(outPutDate)

        } catch (e: Exception) {
            Timber.tag("###BaseProject").d("Retry")
            return Result.retry()
        }
    }

    private suspend fun fetchDataFromNetwork(time: Int) {
        if (time < 5) {
            withContext(Dispatchers.IO) {
                delay(time.toLong())
                // Simulate a long-running network task
                // For example: API calls or database operations
                // Call your networking library like Retrofit, OkHttp, etc.
            }
        } else {
            throw Exception("Time failure")
        }
    }
}
