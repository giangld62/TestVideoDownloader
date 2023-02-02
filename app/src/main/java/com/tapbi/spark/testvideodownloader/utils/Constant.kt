package com.tapbi.spark.testvideodownloader.utils

import android.app.ActivityManager
import android.content.Context


object Constant {
    val YOUTUBE_SHORTS_VIDEO_SCRIPTS =  """
                                            (function(){
                                                let videoUrl = document.querySelector('[rel="canonical"]').getAttribute("href").slice(21)
                                                let thumbImageUrl = document.querySelector("div.carousel-item[aria-hidden='false']").querySelector("div.background-style-black").style.backgroundImage.slice(4, -1).replace(/"/g, "")
                                                return videoUrl + "," + thumbImageUrl
                                            })()
                                        """.trimIndent()

    val YOUTUBE_SCRIPTS_1 = """
                                    (function(){
                                        let videoElements = document.querySelectorAll(".media-item-thumbnail-container")
                                        let currentScrollPos = document.body.scrollTop || document.documentElement.scrollTop
                                        for(let i = 0; i< videoElements.length; i++){
                                            var elementTop = currentScrollPos + videoElements[i].getBoundingClientRect().top
                                            var elementBottom = elementTop + videoElements[i].offsetHeight
                                            if(elementTop - videoElements[i].offsetHeight< currentScrollPos  && currentScrollPos < elementBottom - videoElements[i].offsetHeight){
                                                return videoElements[i].getAttribute("href") + "," + videoElements[i].firstElementChild.childNodes[1].getAttribute("src")
                                            }
                                        }
                                    })();
                                """.trimIndent()

    val YOUTUBE_SCRIPTS_2 = """
                                        (function(){
                                            let videoElements = document.querySelectorAll(".media-item-thumbnail-container")
                                            let currentScrollPos = document.body.scrollTop || document.documentElement.scrollTop
                                            let currentPlayingVideoHeight = document.querySelector("#player-control-overlay").offsetHeight
                                            if(currentPlayingVideoHeight * 2.5 < videoElements[0].getBoundingClientRect().top){
                                                return "Get current playing video"
                                            }
                                            for(let i = 0; i< videoElements.length; i++){
                                                var elementTop = currentScrollPos + videoElements[i].getBoundingClientRect().top
                                                var elementBottom = elementTop + videoElements[i].offsetHeight
                                                if(elementTop - currentPlayingVideoHeight < currentScrollPos  && currentScrollPos < elementBottom - currentPlayingVideoHeight){
                                                    return videoElements[i].getAttribute("href") + "," + videoElements[i].firstElementChild.childNodes[1].getAttribute("src")
                                                }
                                            }
                                        })();
                                    """.trimIndent()

    val YOUTUBE_SCRIPTS_3 = """
                                        (function(){
                                            let videoElements = document.querySelectorAll(".media-item-thumbnail-container")
                                            let currentScrollPos = document.body.scrollTop || document.documentElement.scrollTop
                                            let currentPlayingVideoHeight = document.querySelector("#player-control-overlay").offsetHeight
                                            if(currentPlayingVideoHeight * 2.5 < videoElements[0].getBoundingClientRect().top){
                                                return "Get current playing video"
                                            }
                                            for(let i = 0; i< videoElements.length; i++){
                                                var elementTop = currentScrollPos + videoElements[i].getBoundingClientRect().top
                                                var elementBottom = elementTop + videoElements[i].offsetHeight
                                                if(elementTop - currentPlayingVideoHeight * 2 < currentScrollPos  && currentScrollPos < elementBottom - currentPlayingVideoHeight * 2){
                                                    return videoElements[i].getAttribute("href") + "," + videoElements[i].firstElementChild.childNodes[1].getAttribute("src")
                                                }
                                            }
                                        })();
                                    """.trimIndent()

    const val FACEBOOK_SCRIPT_1 = "(function(){" +
            "let element = document.querySelectorAll(\"div[data-video-id]\")\n" +
            "let currentScrollPos = document.body.scrollTop || document.documentElement.scrollTop\n" +
            "    for (let i = 0; i < element.length; i++) {\n" +
            "        var elementTop = currentScrollPos + element[i].getBoundingClientRect().top;\n" +
            "        var elementBottom = elementTop + element[i].offsetHeight;\n" +
            "        if(elementTop < currentScrollPos + element[i].offsetHeight && currentScrollPos + element[i].offsetHeight < elementBottom){\n" +
            "            return element[i].getAttribute(\"data-video-id\") + \",\" + element[i].firstElementChild.getAttribute(\"src\")+ \",\" +elementTop+ \",\" +elementBottom;\n" +
            "        }\n" +
            "    }\n" +
            "})();"

    val YOUTUBE_SCRIPTS_4 = """
                                    (function(){
                                        let videoUrls = document.querySelectorAll("a.compact-media-item-image")
                                        let firstVideoElement = document.querySelector("ytm-channel-featured-video-renderer")
                                        let currentScrollPos = document.body.scrollTop || document.documentElement.scrollTop
                                        if(firstVideoElement != null){
                                            let firstVideoElementTop = firstVideoElement.getBoundingClientRect().top + currentScrollPos
                                            let firstVideoElementBottom = firstVideoElementTop + firstVideoElement.offsetHeight
                                            if(firstVideoElementTop - firstVideoElement.offsetHeight < currentScrollPos && currentScrollPos < firstVideoElementBottom - firstVideoElement.offsetHeight){
                                                return firstVideoElement.firstElementChild.getAttribute("href") + "," + firstVideoElement.querySelector("img").getAttribute("src")
                                            }
                                        }
                                        for(let i = 0; i< videoUrls.length; i++){
                                            var elementTop = currentScrollPos + videoUrls[i].getBoundingClientRect().top
                                            var elementBottom = elementTop + videoUrls[i].offsetHeight
                                            if(elementTop - videoUrls[i].offsetHeight * 3 < currentScrollPos  && currentScrollPos < elementBottom - videoUrls[i].offsetHeight * 3){
                                                return videoUrls[i].getAttribute("href") + "," + videoUrls[i].firstElementChild.childNodes[1].getAttribute("src")
                                            }
                                        }
                                    })();
                                """.trimIndent()

    val TWITTER_SCRIPTS = """
        (function(){
            let postElements = document.querySelectorAll('div[data-testid="cellInnerDiv"]')
            let currentScrollPos = document.body.scrollTop || document.documentElement.scrollTop
            for(let i = 0; i< postElements.length; i++){
                let imgTag = postElements[i].querySelector('img[alt="Embedded video"]')
                if(imgTag != null){
                    let urlLink = postElements[i].querySelector('a[dir="ltr"]')
                    if(urlLink != null){
                        var elementTop = currentScrollPos + postElements[i].getBoundingClientRect().top
                        var elementBottom = elementTop + postElements[i].offsetHeight
                        return urlLink.getAttribute("href") + "," + imgTag.getAttribute("src")
                        if(elementTop < currentScrollPos  && currentScrollPos < elementBottom ){
                            return urlLink.getAttribute("href") + "," + imgTag.getAttribute("src")
                        }
                    }
                }
            }
        })();
    """.trimIndent()

    fun isAppRunning(context: Context, packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val procInfos = activityManager.runningAppProcesses
        if (procInfos != null) {
            for (processInfo in procInfos) {
                if (processInfo.processName == packageName) {
                    return true
                }
            }
        }
        return false
    }

}