package com.example.sdk.presentation.camera

import android.os.Build
import androidx.camera.view.PreviewView
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import com.example.sdk.enum.ErrorType.CAMERA_LOADING_ERROR
import com.example.sdk.listeners.CameraRequestListener
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R])
class CameraActivityTest {
    private lateinit var scenario: ActivityScenario<CameraPreviewActivity>

    @Before
    fun setup(){
        scenario = launchActivity()
    }

    @Test
    fun `getPreview() is build correctly`(){
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.onActivity { activity ->
            val previewView = PreviewView(activity)
            val preview = activity.getPreview(previewView)
            assertThat(preview).isNotNull()
        }
    }

    @Test
    fun `when updating error message should call listener and finish activity`(){
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.onActivity { activity ->
            val listener = mockk<CameraRequestListener>()
            CameraPreviewActivity.cameraRequestListener = listener
            every { listener.errorProcessingImage(CAMERA_LOADING_ERROR) } just Runs
            activity.updateError(Exception(), CAMERA_LOADING_ERROR)
            verify(exactly = 1) { listener.errorProcessingImage(CAMERA_LOADING_ERROR) }
            confirmVerified(listener)
            assertThat(activity.isFinishing).isTrue()
        }
    }
}