package com.tms.financiapp
import android.app.Activity
import com.google.android.gms.tasks.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.lang.Exception
import java.util.concurrent.Executor

class TaskMock<T>(private val result: T) : Task<T>() {
    override fun isComplete(): Boolean {
        return true
    }

    override fun isSuccessful(): Boolean {
        return true
    }

    override fun getResult(): T {
        return result
    }

    override fun <X : Throwable?> getResult(p0: Class<X>): T {
        return result
    }

    override fun isCanceled(): Boolean {
        return false
    }

    override fun addOnCanceledListener(p0: OnCanceledListener): Task<T> {
        return this
    }

    override fun addOnCanceledListener(p0: Executor, p1: OnCanceledListener): Task<T> {
        return this
    }

    override fun addOnCanceledListener(p0: Activity, p1: OnCanceledListener): Task<T> {
        return this
    }

    override fun addOnCompleteListener(p0: OnCompleteListener<T>): Task<T> {
        p0.onComplete(this)
        return this
    }

    override fun addOnCompleteListener(p0: Executor, p1: OnCompleteListener<T>): Task<T> {
        p1.onComplete(this)
        return this
    }

    override fun addOnCompleteListener(p0: Activity, p1: OnCompleteListener<T>): Task<T> {
        p1.onComplete(this)
        return this
    }

    override fun addOnSuccessListener(p0: OnSuccessListener<in T>): Task<T> {
        p0.onSuccess(result)
        return this
    }

    override fun addOnSuccessListener(p0: Executor, p1: OnSuccessListener<in T>): Task<T> {
        p1.onSuccess(result)
        return this
    }

    override fun addOnSuccessListener(p0: Activity, p1: OnSuccessListener<in T>): Task<T> {
        p1.onSuccess(result)
        return this
    }

    override fun addOnFailureListener(p0: OnFailureListener): Task<T> {
        return this
    }

    override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<T> {
        return this
    }

    override fun getException(): Exception? {
        TODO("Not yet implemented")
    }

    override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<T> {
        return this
    }

}
