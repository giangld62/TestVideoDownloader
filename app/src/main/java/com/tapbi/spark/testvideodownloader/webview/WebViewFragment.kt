package com.tapbi.spark.testvideodownloader.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.tapbi.spark.testvideodownloader.R
import com.tapbi.spark.testvideodownloader.ui.NavActivity
import kotlinx.android.synthetic.main.fragment_web_view.*
import java.io.IOException
import java.io.OutputStreamWriter

class WebViewFragment : Fragment() {

    companion object{
        var videoIds = arrayListOf<String>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as? NavActivity)?.hideNav()
        return inflater.inflate(R.layout.fragment_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        eventClick()
    }

    private fun eventClick() {
        btn_download.setOnClickListener {
            web_view.evaluateJavascript("(function() { return ('<html>'+new XMLSerializer().serializeToString(document)+'</html>'); })();")
            { html ->
//                writeToFile(html,requireContext())
                val pattern = "&quot;video_id&quot;:&quot;\\d*&quot;".toRegex()
                val tempList = pattern.findAll(html)
                tempList.forEach { f ->
                    val m = f.value.replace("&quot;video_id&quot;:&quot;","").replace("&quot;","")
                    videoIds.add(m)
                }
                Log.e("TAG", "eventClick: $videoIds" )
                findNavController().navigate(R.id.home_fragment)
            }
        }
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

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        web_view.settings.javaScriptEnabled = true
        web_view.apply {
            webViewClient = object: WebViewClient(){
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    pb_load.visibility = View.GONE
                }
            }
            loadUrl("https://m.facebook.com")
        }
    }

}