package com.tapbi.spark.testvideodownloader.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.work.*
import com.squareup.picasso.Picasso
import com.tapbi.spark.testvideodownloader.work.DownloadWorker
import com.tapbi.spark.testvideodownloader.work.DownloadWorker.Companion.acodecKey
import com.tapbi.spark.testvideodownloader.work.DownloadWorker.Companion.downloadDirKey
import com.tapbi.spark.testvideodownloader.work.DownloadWorker.Companion.formatIdKey
import com.tapbi.spark.testvideodownloader.work.DownloadWorker.Companion.nameKey
import com.tapbi.spark.testvideodownloader.work.DownloadWorker.Companion.sizeKey
import com.tapbi.spark.testvideodownloader.work.DownloadWorker.Companion.taskIdKey
import com.tapbi.spark.testvideodownloader.work.DownloadWorker.Companion.urlKey
import com.tapbi.spark.testvideodownloader.work.DownloadWorker.Companion.vcodecKey
import com.tapbi.spark.testvideodownloader.R
import com.tapbi.spark.testvideodownloader.adapter.VidInfoAdapter
import com.tapbi.spark.testvideodownloader.adapter.VidInfoListener
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import com.tapbi.spark.testvideodownloader.vm.LoadState
import com.tapbi.spark.testvideodownloader.vm.VidInfoViewModel
import com.tapbi.spark.testvideodownloader.webview.WebViewFragment.Companion.videoIds
import com.tapbi.spark.testvideodownloader.model.VidInfoItem
import com.tapbi.spark.testvideodownloader.ui.HomeFragment.Companion.OPEN_DIRECTORY_REQUEST_CODE
import com.tapbi.spark.testvideodownloader.ui.HomeFragment.Companion.downloadDirPath
import com.tapbi.spark.testvideodownloader.ui.HomeFragment.Companion.downloadLocationDialogTag
import com.tapbi.spark.testvideodownloader.webview.WebViewFragment.Companion.currentVideoId
import com.tapbi.spark.testvideodownloader.webview.WebViewFragment.Companion.urlDown
import com.tapbi.spark.testvideodownloader.webview.WebViewFragment.Companion.urlWeb
import timber.log.Timber


class HomeFragment : Fragment(), SearchView.OnQueryTextListener,
    DownloadPathDialogFragment.DialogListener {
    private var currentVideoPos = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        downloadDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initViews(view)
        eventClick()
    }

    override fun onDestroyView() {
        Timber.e("giangld onDestroyView")
        super.onDestroyView()
    }

    private fun eventClick() {
//        tv_next_video.setOnClickListener {
//            if(currentVideoPos < videoIds.size-1){
//                currentVideoPos ++
//            }
//            else{
//                currentVideoPos = 0;
//            }
//            processSearch("https://facebook.com/username/videos/${videoIds[currentVideoPos]}")
//        }
    }

    private fun initViews(view: View) {
        if(urlWeb.value?.startsWith("https://m.facebook.com") == true){
            processSearch("https://facebook.com/$currentVideoId")
        }
        else if(urlWeb.value?.startsWith("https://www.tiktok.com") == true){
            processSearch(urlDown)
        }
        else if(urlWeb.value?.startsWith("https://m.youtube.com/watch?v=") == true){
            processSearch(urlDown)
        }
        else if(urlWeb.value?.startsWith("https://m.youtube.com") == true){
            processSearch("https://m.youtube.com$currentVideoId")
        }
        else if(urlWeb.value?.startsWith("https://mobile.twitter.com/") == true){
            processSearch("https://twitter.com$currentVideoId")
        }
        val vidFormatsVm =
            ViewModelProvider(activity as MainActivity)[VidInfoViewModel::class.java]
        with(view.recyclerview) {
            adapter =
                VidInfoAdapter(VidInfoListener listener@{
                    vidFormatsVm.selectedItem = it
                    if (!isStoragePermissionGranted()) {
                        return@listener
                    }
                    DownloadPathDialogFragment().show(
                        childFragmentManager,
                        downloadLocationDialogTag
                    )

                })
        }
        vidFormatsVm.vidFormats.observe(viewLifecycleOwner, Observer { t ->
            t?.let {
                (recyclerview.adapter as VidInfoAdapter).fill(t)
                vidFormatsVm.vidFormats.postValue(null)
            }
        })
        vidFormatsVm.loadState.observe(viewLifecycleOwner, Observer { t ->
            when (t) {
                LoadState.INITIAL -> {
                    loading_pb.visibility = GONE
                }
                LoadState.LOADING -> {
                    loading_pb.visibility = VISIBLE
                    start_tv.visibility = GONE
                }
                LoadState.LOADED -> {
                    loading_pb.visibility = GONE
                    start_tv.visibility = GONE
                }
            }
        })
        vidFormatsVm.thumbnail.observe(viewLifecycleOwner, Observer {
            it?.apply {
                currentThumbImageLink = this
                val picasso = Picasso.get()
                picasso.load(this)
                    .into(toolbar_image)
                vidFormatsVm.thumbnail.postValue(null)
            } ?: toolbar_image.setImageResource(R.drawable.toolbar)
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.search).isVisible = true
        (activity as MainActivity).supportActionBar?.themedContext?.let {
            val searchView = SearchView(requireContext())
            menu.findItem(R.id.search).actionView = searchView
            searchView.setOnQueryTextListener(this)
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        processSearch(query!!)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun processSearch(url: String) {
        val vidFormatsVm =
            ViewModelProvider(activity as MainActivity).get(VidInfoViewModel::class.java)
        vidFormatsVm.fetchInfo(url)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            OPEN_DIRECTORY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let {
                        activity?.contentResolver?.takePersistableUriPermission(
                            it,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                        setDefaultDownloadLocation(it.toString())
                        val vidFormatsVm =
                            ViewModelProvider(activity as MainActivity).get(VidInfoViewModel::class.java)
                        startDownload(vidFormatsVm.selectedItem, it.toString())
                    }
                }
            }
        }
    }

    // sets default download location if unset
    private fun setDefaultDownloadLocation(path: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        prefs.getString(getString(R.string.download_location_key), null) ?: prefs.edit()
            .putString(getString(R.string.download_location_key), path).apply()
    }

    private fun startDownload(vidFormatItem: VidInfoItem.VidFormatItem, downloadDir: String) {
        val vidInfo = vidFormatItem.vidInfo
        val vidFormat = vidFormatItem.vidFormat
        val workTag = vidInfo.id
        val workManager = WorkManager.getInstance(activity?.applicationContext!!)
        val state =
            workManager.getWorkInfosByTag(workTag).get()?.getOrNull(0)?.state
        val running = state === WorkInfo.State.RUNNING || state === WorkInfo.State.ENQUEUED
        if (running) {
            Toast.makeText(
                context,
                R.string.download_already_running,
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val workData = workDataOf(
            urlKey to vidInfo.webpageUrl,
            nameKey to vidInfo.title,
            formatIdKey to vidFormat.formatId,
            acodecKey to vidFormat.acodec,
            vcodecKey to vidFormat.vcodec,
            downloadDirKey to downloadDir,
            sizeKey to vidFormat.fileSize,
            taskIdKey to vidInfo.id
        )
        Log.e("TAG", "startDownload: " + vidInfo.webpageUrl )
        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .addTag(workTag)
            .setInputData(workData)
            .build()

        workManager.enqueueUniqueWork(
            workTag,
            ExistingWorkPolicy.KEEP,
            workRequest
        )
        Toast.makeText(
            context,
            R.string.download_queued,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onOk(dialog: DownloadPathDialogFragment) {
        val vidFormatsVm =
            ViewModelProvider(activity as MainActivity).get(VidInfoViewModel::class.java)
        val path = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString(getString(R.string.download_location_key), null)
        if (path == null) {
            Toast.makeText(context, R.string.invalid_download_location, Toast.LENGTH_SHORT).show()
            return
        }
//        val dir = File(requireContext().filesDir,"test")
//        if(!dir.exists()){
//            dir.mkdir()
//        }
//        val path = dir.path
        startDownload(vidFormatsVm.selectedItem, path)
    }

    override fun onFilePicker(dialog: DownloadPathDialogFragment) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        }
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)
    }

    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            DownloadPathDialogFragment().show(
                childFragmentManager,
                downloadLocationDialogTag
            )
        }
    }

    companion object {
        const val downloadLocationDialogTag = "download_location_chooser_dialog"
        private const val OPEN_DIRECTORY_REQUEST_CODE = 42069
        var downloadDirPath: String? = ""
        var currentThumbImageLink = ""
    }

}
