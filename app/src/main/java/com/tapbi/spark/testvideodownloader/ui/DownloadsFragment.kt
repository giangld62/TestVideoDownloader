package com.tapbi.spark.testvideodownloader.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.tapbi.spark.testvideodownloader.R
import com.tapbi.spark.testvideodownloader.adapter.DownloadsAdapter
import com.tapbi.spark.testvideodownloader.adapter.ViewPager2Adapter
import com.tapbi.spark.testvideodownloader.database.Download
import com.tapbi.spark.testvideodownloader.vm.DownloadsViewModel
import kotlinx.android.synthetic.main.fragment_downloads_list.*
import timber.log.Timber


class DownloadsFragment : Fragment() {

    private var isMultiSelect = false
    private lateinit var downloadsViewModel: DownloadsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_downloads_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        eventClick()
    }

    private fun eventClick() {
        tv_select.setOnClickListener {
            isMultiSelect = !isMultiSelect
            downloadsViewModel.isMultiSelectLiveData.postValue(isMultiSelect)
            tv_select.text = if(isMultiSelect) "Cancel" else "Select"
        }
    }

    private fun setUpView() {
        downloadsViewModel = ViewModelProvider(activity as MainActivity)[DownloadsViewModel::class.java]
        val tabs = listOf("Video", "Music")
        val icons = listOf(ContextCompat.getDrawable(requireContext(),R.drawable.video_btn),ContextCompat.getDrawable(requireContext(),R.drawable.tablayout_music_icon))
        viewPager.adapter = ViewPager2Adapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, index ->
            tab.text = tabs[index]
        }.attach()

    }

}
