package com.inventory.testapplication

import android.content.Context
import android.content.Intent

inline fun <reified T> Context.navigateToActivity(
    clearStack: Boolean = false,
    extras: Intent.() -> Unit = {}
) {
    val intent = Intent(this, T::class.java)
    intent.extras()  // Apply any extras passed via the lambda
    if (clearStack) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    startActivity(intent)
}


/*
navigateToActivity<SecondActivity>()

// Navigation with extras
navigateToActivity<SecondActivity> {
    putStringExtra("KEY_NAME", "Some Value")
    putIntExtra("KEY_AGE", 30)
}

// Clear activity stack and navigate to another screen
navigateToActivity<LoginActivity>(clearStack = true)*/
