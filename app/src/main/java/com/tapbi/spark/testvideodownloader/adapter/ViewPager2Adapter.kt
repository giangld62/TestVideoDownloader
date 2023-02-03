package com.tapbi.spark.testvideodownloader.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tapbi.spark.testvideodownloader.ui.music.DownloadedMusicsFragment
import com.tapbi.spark.testvideodownloader.ui.video.DownloadedVideosFragment

class ViewPager2Adapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if(position == 0) DownloadedVideosFragment() else DownloadedMusicsFragment()
    }
}