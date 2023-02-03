package com.tapbi.spark.testvideodownloader.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tapbi.spark.testvideodownloader.model.VidInfoItem
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.mapper.VideoInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class VidInfoViewModel : ViewModel() {

    val vidFormats: MutableLiveData<VideoInfo?> = MutableLiveData()
    val loadState: MutableLiveData<LoadState> = MutableLiveData(LoadState.INITIAL)
    val thumbnail: MutableLiveData<String> = MutableLiveData()
    lateinit var selectedItem: VidInfoItem.VidFormatItem

    private fun submit(vidInfoItems: VideoInfo?) {
        vidFormats.postValue(vidInfoItems)
    }

    private fun updateLoading(loadState: LoadState) {
        this.loadState.postValue(loadState)
    }

    private fun updateThumbnail(thumbnail: String?) {
        this.thumbnail.postValue(thumbnail)
    }

    fun fetchInfo(url: String) {
        viewModelScope.launch {
            updateLoading(LoadState.LOADING)
            submit(null)
            updateThumbnail(null)
            lateinit var vidInfo: VideoInfo
            try {
                withContext(Dispatchers.IO) {
                    vidInfo = YoutubeDL.getInstance().getInfo(url)
                    Timber.e("giangld fetchInfo success $url")
                    for(item in vidInfo.formats){
                        Timber.e("giangld fetchInfo success ${item.ext}")
                    }
                }
            } catch (e: Exception) {
                Timber.e("giangld fetchInfo error: $url")
                Timber.e("giangld fetchInfo error: ${e.message}")
                updateLoading(LoadState.LOADED)
                return@launch
            }

            updateLoading(LoadState.LOADED)
            updateThumbnail(vidInfo.thumbnail)
            submit(vidInfo)
        }
    }

}

enum class LoadState {
    INITIAL, LOADING, LOADED
}
