package com.tapbi.spark.testvideodownloader.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tapbi.spark.testvideodownloader.R
import com.tapbi.spark.testvideodownloader.adapter.VideoAdapter
import kotlinx.android.synthetic.main.bottom_sheet_video_list.*

class VideoListBottomSheet(private val adapter: VideoAdapter) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_video_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvVideoList.adapter = adapter
        super.onViewCreated(view, savedInstanceState)
    }
}