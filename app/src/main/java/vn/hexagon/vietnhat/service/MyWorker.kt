package vn.hexagon.vietnhat.service

import android.content.Context
import androidx.work.ListenableWorker

import androidx.work.Worker
import androidx.work.WorkerParameters
import vn.hexagon.vietnhat.base.utils.DebugLog

/*
 * Create by VuNBT on 2019-10-30 
 */
class MyWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): ListenableWorker.Result {
        DebugLog.d("Performing long running task in scheduled job")
        // TODO(developer): add long running task here.
        return ListenableWorker.Result.success()
    }
}