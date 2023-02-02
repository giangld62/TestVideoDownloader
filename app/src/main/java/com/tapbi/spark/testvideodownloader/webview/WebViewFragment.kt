package com.tapbi.spark.testvideodownloader.webview

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.JsonReader
import android.util.JsonToken
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.tapbi.spark.testvideodownloader.R
import com.tapbi.spark.testvideodownloader.adapter.VideoAdapter
import com.tapbi.spark.testvideodownloader.model.VidInfo
import com.tapbi.spark.testvideodownloader.ui.NavActivity
import com.tapbi.spark.testvideodownloader.ui.dialog.VideoListBottomSheet
import com.tapbi.spark.testvideodownloader.utils.AudioExtractor
import com.tapbi.spark.testvideodownloader.utils.Constant.FACEBOOK_SCRIPT_1
import com.tapbi.spark.testvideodownloader.utils.Constant.TWITTER_SCRIPTS
import com.tapbi.spark.testvideodownloader.utils.Constant.YOUTUBE_SCRIPTS_1
import com.tapbi.spark.testvideodownloader.utils.Constant.YOUTUBE_SCRIPTS_2
import com.tapbi.spark.testvideodownloader.utils.Constant.YOUTUBE_SCRIPTS_3
import com.tapbi.spark.testvideodownloader.utils.Constant.YOUTUBE_SCRIPTS_4
import com.tapbi.spark.testvideodownloader.utils.Constant.YOUTUBE_SHORTS_VIDEO_SCRIPTS
import it.sauronsoftware.jave.*
import kotlinx.android.synthetic.main.fragment_downloads_list.*
import kotlinx.android.synthetic.main.fragment_web_view.*
import org.jsoup.Jsoup
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.StringReader


class WebViewFragment : Fragment() {

    companion object {
        var videoIds = arrayListOf<String>()
        var urlDown: String = ""
        var currentVideoId = ""
        var urlWeb = MutableLiveData<String?>()
    }

    private var htmlContent = StringBuilder()
    var adapter: VideoAdapter? = null
    var currentBlobLink = ""
    var links = arrayListOf<String>()
    var link2s = arrayListOf<String>()
    var blobLinks = arrayListOf<String>()
    var link3s = arrayListOf<String>()
    private var clipBoard: ClipboardManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as? NavActivity)?.hideNav()
//        clipBoard = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
//        clipBoard?.addPrimaryClipChangedListener {
//            Toast.makeText(requireContext(), "clip board change ${clipBoard?.text}", Toast.LENGTH_SHORT).show()
//        }
        return inflater.inflate(R.layout.fragment_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        eventClick()
        observeData()
    }

    private fun observeData() {
        urlWeb.observe(viewLifecycleOwner) {
            it?.let {
                if (!it.startsWith("https://m.youtube.com/watch?v=")) {
                    iv_download2.visibility = View.GONE
                    iv_download.visibility = View.VISIBLE
                    Glide.with(iv_download).load(R.drawable.ic_baseline_arrow_circle_down_24)
                        .circleCrop()
                        .into(iv_download)
                }
            }
        }
    }

    private fun eventClick() {
        iv_back.setOnClickListener {
//            testExtractAudio()
//            testExtractAudioUseFFmpeg()
//            testExtractAudioJAVE()
            if (web_view.canGoBack()) {
                web_view.goBack()
            }
        }

        imLoad.setOnClickListener {
            val url = edUrl.text.toString()
            if (Patterns.WEB_URL.matcher(url).matches()) {
                web_view.loadUrl(url)
            } else {
                web_view.loadUrl("https://www.google.com/search?q=$url")
            }
        }

        iv_download.setOnClickListener {
//            val mPlayer = MediaPlayer.create(requireContext(), Uri.parse(File(requireContext().filesDir,"extract.mp3").path))
//            mPlayer.start()
//            context.sendBroadcast( Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            if (currentVideoId.isNotEmpty()) {
                findNavController().navigate(R.id.home_fragment)
//            getLinkTiktokTest()
//            clickDownload()
            }
        }

        iv_download2.setOnClickListener {
            if (urlDown.isNotEmpty()) {
                findNavController().navigate(R.id.home_fragment)
            }
        }
    }

    private fun testExtractAudio() {
        val videoFile = File(requireContext().filesDir, "a.mp4").path
        val originalAudio = File(requireContext().filesDir, "extract_webm_video.mp3").path
        AudioExtractor().genVideoUsingMuxer(videoFile, originalAudio, -1, -1, true, false)
    }

//    private fun testExtractAudioUseFFmpeg() {
//        Timber.e("giangld testExtractAudioUseFFmpeg")
//        val videoFile = File(requireContext().filesDir, "test_2.webm").path
//        val originalAudio = File(requireContext().filesDir, "extract123.m4a").path
//        val c1 = arrayOf(
//            "-i",
//            videoFile,
//            "-vn",
//            "-acodec",
//            "copy",
//            originalAudio
//        )
//        val c = "ffmpeg -i $videoFile -vn -acodec copy $originalAudio"
//        val commandArray = c.split(" ").toTypedArray()
//        com.arthenica.mobileffmpeg.FFmpeg.executeAsync(c1, object : ExecuteCallback {
//            override fun apply(executionId: Long, returnCode: Int) {
//                Timber.e("giangld return $returnCode")
//                Timber.e("giangld executionId $executionId")
//                Timber.e("giangld FFMPEG   ${FFmpegExecution(executionId, arrayOf(c))}")
//            }
//        })
//    }

    private fun testExtractAudioJAVE() {
        val source = File(requireContext().filesDir, "test.mp4")
        val target = File(requireContext().filesDir,"target.mp3")

        val audioAttributes = AudioAttributes()
        audioAttributes.setCodec("libmp3lame")
        audioAttributes.setBitRate(128000)
        audioAttributes.setChannels(2)
        audioAttributes.setSamplingRate(44100)

        val encodingAttributes = EncodingAttributes()
        encodingAttributes.setFormat("mp3")
        encodingAttributes.setAudioAttributes(audioAttributes)

        val encoder = Encoder()
        encoder.encode(source, target, encodingAttributes,object: EncoderProgressListener{
            override fun sourceInfo(p0: MultimediaInfo?) {
                Timber.e("giangld sourceInfo $p0")
            }

            override fun progress(p0: Int) {
                Timber.e("giangld progress $p0")
            }

            override fun message(p0: String?) {
                Timber.e("giangld message $p0")
            }
        })
    }


    private fun clickDownload() {
        Timber.e("hoangLd click link current: ${web_view.url} url: $urlDown")
        if (urlWeb.value?.startsWith("https://m.facebook.com/reel/") == true) {
            //                getVideoInfoUseJavascript()
            //                getVideoInfoUsingJsoup()
            downloadVideo()
        } else if (urlWeb.value?.startsWith("https://m.facebook.com/") == true) {
            downloadVideo()
        } else if (urlWeb.value?.startsWith("https://www.tiktok.com/") == true) {
            //                web_view.reload()
            findNavController().navigate(R.id.home_fragment)
        }
    }

    private fun getLinkTiktokTest() {
        for (i in links.indices) {
            Timber.e("giangld video ${i + 1}: ${links[i]}")
        }
        Timber.e("\ngiangld ####################################\n")
        for (i in link2s.indices) {
            Timber.e("giangld link2s ${i + 1}: ${link2s[i]}")
        }
        Timber.e("\ngiangld ####################################\n")
        for (i in link3s.indices) {
            Timber.e("giangld link3s ${i + 1}: ${link3s[i]}")
        }
        Timber.e("\ngiangld ####################################\n")
        for (i in blobLinks.indices) {
            Timber.e("giangld blobLinks ${i + 1}: ${blobLinks[i]}")
        }
    }

    private fun downloadVideo() {
        if (currentVideoId.isNotEmpty()) {
            findNavController().navigate(R.id.home_fragment)
        }
    }

    private fun getVideoInfoUseJavascript() {
        val videoInfoList = arrayListOf<VidInfo>()
        web_view.evaluateJavascript(
            """
                (function(){
                    let videoElements = document.querySelectorAll("div[data-video-id]")
                    let videoInfoList = ""
                    for(let i = 0; i< videoElements.length; i++){
                        let thumbImageLink = videoElements[i].firstElementChild.getAttribute("src")
                        let videoId = videoElements[i].getAttribute("data-video-id")
                        let titleTag = videoElements[i].parentElement.lastChild
                        let videoTitle = "Video Number " + (i+1)
                        if(titleTag != null){
                            videoTitle = titleTag.textContent
                        }
                        videoInfoList = videoInfoList + videoId + "," + thumbImageLink + "," + videoTitle + "***"
                    }
                    return videoInfoList
                })()
            """.trimIndent()
        ) {
            val a = it.split("***")
            for (item in a) {
                val tempString = item.replace("\"", "").replace("[", "").replace("]", "")
                Timber.e("giangld ${tempString}")
                if (tempString.isNotEmpty()) {
                    val tempList = tempString.split(",")
                    videoInfoList.add(
                        VidInfo(
                            videoId = tempList[0],
                            videoImageThumbnailLink = tempList[1],
                            title = tempList[2]
                        )
                    )
                }
            }
            Timber.e("giangld videoInfoList.size ${videoInfoList.size}")
        }
        adapter = VideoAdapter(videoInfoList, object : VideoAdapter.IVideoAdapter {
            override fun onItemClick(videoLink: String) {
                videoIds.clear()
                videoIds.add(videoLink)
                findNavController().navigate(R.id.home_fragment)
            }
        })
        val bottomSheet = VideoListBottomSheet(adapter!!)
        bottomSheet.show(childFragmentManager, null)
    }

    private fun writeToFile(data: String, context: Context) {
        try {
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput("config.txt", MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
        }
    }

    private fun findVideoId(html: String) {
        if (web_view.url!!.startsWith("https://m.facebook.com/")) {

            val pattern = "&quot;video_id&quot;:&quot;\\d*&quot;".toRegex()
            val tempList = pattern.findAll(html)
            tempList.forEach { f ->
                val m = f.value.replace("&quot;video_id&quot;:&quot;", "").replace("&quot;", "")
                videoIds.add(m)
                Timber.e("hoangLd id https://facebook.com/$m ")
            }
            if (videoIds.size > 0) {
                //findNavController().navigate(R.id.home_fragment)
            }
        } else {
            videoIds.clear()
            videoIds.add(web_view.url!!)
            findNavController().navigate(R.id.home_fragment)
        }
    }

    private fun getVideoInfoUsingJsoup() {
        getHtmlContent()
        val doc = Jsoup.parse(htmlContent.toString())
        val videoElements = doc.select("div[data-video-id]")
        val videoInfoList = arrayListOf<VidInfo>()
        for (item in videoElements) {
            val imgTag = item.firstElementChild()
            val title = item.parent()?.lastElementChild()?.text()
            videoInfoList.add(
                VidInfo(
                    videoId = item.attr("data-video-id"),
                    videoImageThumbnailLink = imgTag?.attr("src"),
                    title = title
                )
            )
        }
        val bottomSheet = VideoListBottomSheet(
            VideoAdapter(videoInfoList,
                object : VideoAdapter.IVideoAdapter {
                    override fun onItemClick(videoLink: String) {
                        currentVideoId = videoLink
                        findNavController().navigate(R.id.home_fragment)
                    }
                })
        )
        bottomSheet.show(childFragmentManager, null)
    }

    private fun getHtmlContent() {
        web_view.evaluateJavascript("(function() { return ('<html>'+new XMLSerializer().serializeToString(document)+'</html>'); })();") { html ->
            htmlContent.clear()
            val reader = JsonReader(StringReader(html))
            reader.isLenient = true
            try {
                if (reader.peek() == JsonToken.STRING) {
                    val domStr = reader.nextString()
                    domStr?.let {
                        htmlContent.append(it)
                    }
                }
            } catch (e: IOException) {
                // handle exception
            } finally {
                reader.close()
            }
            //                writeToFile(htmlContent.toString(),requireContext())
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        web_view.settings.javaScriptEnabled = true
        web_view.apply {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
//                    pb_load.visibility = View.GONE
                }

                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    if (request?.url?.toString()?.contains("mp4") == true) {
                        if (!link3s.contains(request.url.toString()) && !request.url.toString()
                                .endsWith("40000")
                        ) {
                            link3s.add(request.url.toString())
                        }
                    }
                    return super.shouldInterceptRequest(view, request)
                }


                override fun onLoadResource(view: WebView?, url: String?) {
                    super.onLoadResource(view, url)
                    if (url == null) {
                        return
                    }
                    if (urlWeb.value?.startsWith("https://www.tiktok.com/", true) == true) {
                        //check tiktok
                        if (url.startsWith("https://v16-webapp.tiktok.com/", false)) {
//                            Timber.e("giangld tiktok link: ${url}")
                            if (!link2s.contains(url) && !url.endsWith("40000")) {
                                link2s.add(url)
                            }
//                            getTiktokVideoId(url)
//                            getTiktokVideoThumbImage()
//                            urlDown = url
//                            Timber.e("giangld ${urlDown}")
                        } else if (url.contains("mp4")) {
                            if (!links.contains(url)) {
                                links.add(url)
                            }
                        } else if (!blobLinks.contains(url)) {
                            blobLinks.add(url)
                        }
                    } else if (urlWeb.value?.startsWith("https://m.youtube.com/shorts/") == true) {
                        //check youtube
                        if (isAdded) {
                            web_view.evaluateJavascript(YOUTUBE_SHORTS_VIDEO_SCRIPTS) {
                                getVideoIdAndThumbImage(it)
                                val a = "https://m.youtube.com/shorts/cBmow0TTcmg"
                            }
                        }
                    } else if (urlWeb.value?.startsWith("https://m.facebook.com/reel/") == true) {
                        if (url.startsWith("https://scontent.fhan") && url.contains(".mp4?")) {
                            Timber.e("giangld ${web_view.url} - $url")
                            urlDown = url
                            web_view.evaluateJavascript(
                                """
                                    (function(){
                                        let videoTag = document.querySelector('[data-video-url="$urlDown"]')
                                        return videoTag.getAttribute("data-video-id") + "," + videoTag.firstElementChild.getAttribute("src")
                                    })()
                                """.trimIndent()
                            ) {
                                val tempList = it.replace("\"", "").split(",")
                                Glide.with(iv_download).load(tempList[1]).circleCrop()
                                    .into(iv_download)
                                currentVideoId = tempList[0]
                            }
                        }
                    } else {
                        //other web
                        if (url.endsWith(".m3u8", true) || url.endsWith(
                                ".mp4", true
                            )
                        ) {
//                            Timber.e("hoangLd url $url")
                            urlDown = url
                        } else if (url.contains(".mp4?", true)) {
                            Timber.e("hoangLd url $url")
                            urlDown = url
                        }
                    }
                }


                override fun doUpdateVisitedHistory(
                    view: WebView?, url: String?, isReload: Boolean
                ) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    url?.let {
                        urlWeb.value = url
                        val editable = Editable.Factory.getInstance().newEditable(url)
                        edUrl.text = editable
                        currentVideoId = ""

                        if (urlWeb.value?.startsWith("https://m.youtube.com/watch?v=") == true) {
                            //check youtube
                            urlDown = if (url.contains("&")) url.split("&")[0] else url
                            Timber.e("giangld urlDown ${urlDown}")
                            iv_download.visibility = View.GONE
                            iv_download2.visibility = View.VISIBLE
                            iv_download2.apply {
                                setBackgroundResource(R.drawable.download_found_anim)
                                (background as AnimationDrawable).start()
                            }
                        }
                    }
                }

                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)
                    Timber.e("hoangLd onPageCommitVisible $url")
                }
            }
//            loadUrl("https://www.google.com/")
//            loadUrl("https://m.facebook.com")
            loadUrl("https://m.youtube.com")
//            loadUrl("https://mobile.twitter.com/")
//            loadUrl("https://www.tiktok.com/")
            //loadUrl("https://www.24h.com.vn/bong-da/video-bong-da-chelsea-man-city-dinh-cao-dau-tri-buoc-ngoat-thay-nguoi-ngoai-hang-anh-c48a1430639.html")

//            val summary = "<html><body>You scored <b>192</b> points.</body></html>"
//            loadData(summary, "text/html; charset=utf-8", "utf-8")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
                    if (urlWeb.value?.startsWith("https://m.facebook.com/") == true) {
                        getFbVideoInfo()
                    } else if (urlWeb.value == "https://m.youtube.com/") {
                        getYoutubeHomePageVideoInfo()
                    } else if (urlWeb.value?.startsWith("https://m.youtube.com/watch?v=") == true) {
                        getYoutubeVideoInfo(
                            scrollY,
                            oldScrollY,
                            YOUTUBE_SCRIPTS_2,
                            YOUTUBE_SCRIPTS_3
                        )
                    } else if (urlWeb.value?.startsWith("https://m.youtube.com/@") == true || urlWeb.value?.startsWith(
                            "https://m.youtube.com/playlist"
                        ) == true || urlWeb.value?.startsWith("https://m.youtube.com/channel") == true
                    ) {
                        Timber.e("giangld https://m.youtube.com/@")
                        getYoutubeVideoInfo(
                            scrollY,
                            oldScrollY,
                            YOUTUBE_SCRIPTS_4,
                            YOUTUBE_SCRIPTS_4
                        )
                    } else if (urlWeb.value?.startsWith("https://mobile.twitter.com/") == true) {
                        getTwitterVideoInfo()
                    }
                }
            }
        }
    }

    private fun WebView.getTwitterVideoInfo() {
        evaluateJavascript(
            TWITTER_SCRIPTS
        ) {
            getVideoIdAndThumbImage(it)
        }
    }

    private fun WebView.getYoutubeVideoInfo(
        scrollY: Int,
        oldScrollY: Int,
        scriptsScrollUp: String,
        scriptsScrollDown: String
    ) {
        if (scrollY > oldScrollY) {
            evaluateJavascript(
                scriptsScrollUp
            ) {
                getYoutubeVideoInfoWhenHaveVideoPlaying(it)
            }
        } else {
            evaluateJavascript(
                scriptsScrollDown
            ) {
                getYoutubeVideoInfoWhenHaveVideoPlaying(it)
            }
        }
    }

    private fun getYoutubeVideoInfoWhenHaveVideoPlaying(it: String) {
        if (it == "\"Get current playing video\"") {
            Timber.e("giangld Get current playing video")
            iv_download.visibility = View.GONE
            iv_download2.visibility = View.VISIBLE
            iv_download2.apply {
                setBackgroundResource(R.drawable.download_found_anim)
                (background as AnimationDrawable).start()
            }
            currentVideoId = ""
            urlWeb.value?.let { urlWeb ->
                urlDown = urlWeb
            }
        } else {
            Timber.e("giangld getVideoIdAndThumbImage $it")
            getVideoIdAndThumbImage(it)
        }
    }

    private fun WebView.getYoutubeHomePageVideoInfo() {
        evaluateJavascript(
            YOUTUBE_SCRIPTS_1
        ) {
            getVideoIdAndThumbImage(it)
        }
    }

    private fun WebView.getFbVideoInfo() {
        evaluateJavascript(
            FACEBOOK_SCRIPT_1
        ) {
            getVideoIdAndThumbImage(it)
        }
    }

    private fun getVideoIdAndThumbImage(it: String) {
        val tempList = it.replace("\"", "").split((","))
        Timber.e("giangld ${it}")
        if (it != "null" && currentVideoId != tempList[0]) {
            urlDown = ""
            currentVideoId = tempList[0]
            iv_download.visibility = View.VISIBLE
            iv_download2.visibility = View.GONE
            Glide.with(iv_download).load(tempList[1]).circleCrop()
                .into(iv_download)
            Timber.e("giangld videoId ${tempList[0]} - imgLink: ${tempList[1]}")
        }
    }

    private fun getTiktokVideoId(url: String?) {
        web_view.evaluateJavascript(
            """
                (function(){
                    let a = document.querySelector("#SIGI_STATE").textContent
                    var regex = /"id":"\d+","height"/g
                    return a.match(regex)
                })()
            """.trimIndent()
        ) { html ->
//            writeToFile(html,requireContext())
            val regex = "\\d+".toRegex()
            val tempList = regex.findAll(html).map { it.value }.toList()
            for (item in tempList.distinct()) {
                Timber.e("giangld https://www.tiktok.com/@123/video/$item *** $url")
            }
            Timber.e("################################################### tempList.size: ${tempList.size}")
        }
    }

    private fun getTiktokVideoThumbImage() {
        web_view.evaluateJavascript(
            """
                (function(){
                return window.location.href
                    let a = document.querySelector(".currentPlayingVideo")
                    return a.getAttribute("src") + "***" + a.parentElement.nextElementSibling.nextElementSibling.getAttribute("src")
                })()
            """.trimIndent()
        ) {
            if (it != "null") {
//                Timber.e("giangld ${it}")
            }
        }
    }


}