package com.tapbi.spark.testvideodownloader.ui.video

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.tapbi.spark.testvideodownloader.R
import com.tapbi.spark.testvideodownloader.adapter.DownloadsAdapter
import com.tapbi.spark.testvideodownloader.database.Download
import com.tapbi.spark.testvideodownloader.ui.MainActivity
import com.tapbi.spark.testvideodownloader.vm.DownloadsViewModel
import com.tapbi.spark.testvideodownloader.work.DeleteWorker
import kotlinx.android.synthetic.main.fragment_downloads_list.*
import kotlinx.android.synthetic.main.layout_recycler_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DownloadedVideosFragment: Fragment() {
    private lateinit var downloadsViewModel: DownloadsViewModel
    private lateinit var adapter: DownloadsAdapter
    private var listVideoSelected = arrayListOf<Download>()
    private var downloadVideosList = listOf<Download>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        observerData()
        eventClick()
    }

    private fun eventClick() {
        ivDelete.setOnClickListener {
            if(listVideoSelected.isNotEmpty()){
                for(item in listVideoSelected){
                    startDelete(item.id,requireContext())
                }
                listVideoSelected.clear()
                layoutFunction.visibility = View.GONE
            }
        }
    }

    private fun observerData() {
        downloadsViewModel.isMultiSelectLiveData.observe(viewLifecycleOwner){
            it?.let {
                adapter.setSelectVideos(it)
                downloadsViewModel.isMultiSelectLiveData.postValue(null)
                if(!it){
                    listVideoSelected.clear()
                    layoutFunction.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        rv_videos.layoutManager = GridLayoutManager(requireContext(),2)
        adapter = DownloadsAdapter(object : DownloadsAdapter.IDownloadsAdapter{
            override fun selectVideo(download: Download) {
                listVideoSelected.add(download)
                if(layoutFunction.visibility == View.GONE){
                    layoutFunction.visibility = View.VISIBLE
                }
                tvSelectedCount.text = "Selected ${listVideoSelected.size} Videos"
            }

            override fun unSelectVideo(download: Download) {
                listVideoSelected.remove(download)
                if(layoutFunction.visibility == View.VISIBLE && listVideoSelected.size == 0){
                    layoutFunction.visibility = View.GONE
                }
                tvSelectedCount.text = "Selected ${listVideoSelected.size} Videos"
            }
        })
        rv_videos.adapter = adapter
//        adapter.addItems(fakeData())
        downloadsViewModel = ViewModelProvider(activity as MainActivity)[DownloadsViewModel::class.java]
        downloadsViewModel.allDownloads.observe(viewLifecycleOwner, Observer { downloads ->
            downloadVideosList = downloads
            downloads?.let { adapter.addItems(downloads) }
        })
    }

    private fun fakeData(): ArrayList<Download>{
        val fakeList = arrayListOf<Download>()
        for(i in 0..100){
            fakeList.add(Download(
                name = "Fake Video Number $i",
                timestamp = 0,
                totalSize = 0,
                thumbImageLink = ""
            ))
        }
        return fakeList
    }

    private fun startDelete(id: Long, context: Context) {
        val workTag = "tag_$id"
        val workManager = WorkManager.getInstance(context.applicationContext!!)
        val state = workManager.getWorkInfosByTag(workTag).get()?.getOrNull(0)?.state

        if (state === WorkInfo.State.RUNNING || state === WorkInfo.State.ENQUEUED) {
            return
        }

        val workData = workDataOf(
            DeleteWorker.fileIdKey to id
        )
        val workRequest = OneTimeWorkRequestBuilder<DeleteWorker>()
            .addTag(workTag)
            .setInputData(workData)
            .build()

        workManager.enqueueUniqueWork(
            workTag,
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

    override fun onResume() {
        super.onResume()
        val tempList = arrayListOf<Download>()
        tempList.addAll(downloadVideosList)
        for(item in downloadVideosList){
            val downloadedFile = DocumentFile.fromSingleUri(requireContext(), Uri.parse(item.downloadedPath))!!
            if(!downloadedFile.exists()){
                tempList.remove(item)
                downloadsViewModel.delete(item)
            }
        }
        adapter.addItems(tempList)
    }
}