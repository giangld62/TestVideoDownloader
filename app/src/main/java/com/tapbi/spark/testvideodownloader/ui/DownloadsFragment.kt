package com.tapbi.spark.testvideodownloader.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.testvideodownloader.R
import com.tapbi.spark.testvideodownloader.adapter.DownloadsAdapter
import com.tapbi.spark.testvideodownloader.vm.DownloadsViewModel


class DownloadsFragment : Fragment() {

    private lateinit var downloadsViewModel: DownloadsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_downloads_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                adapter = DownloadsAdapter()
            }
            downloadsViewModel = ViewModelProvider(this).get(DownloadsViewModel::class.java)
            downloadsViewModel.allDownloads.observe(viewLifecycleOwner, Observer { downloads ->
                downloads?.let { (view.adapter as DownloadsAdapter).addItems(downloads) }
            })
        }

        return view
    }

}
