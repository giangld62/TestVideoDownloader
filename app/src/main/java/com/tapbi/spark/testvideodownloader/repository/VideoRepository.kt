package com.tapbi.spark.testvideodownloader.repository

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import com.tapbi.spark.testvideodownloader.R
import timber.log.Timber
import java.util.*

class VideoRepository {
//    fun getItemVideoAlbum(mActivity: Activity, uri: Uri?): AlbumDetail? {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(
//                    mActivity,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                )
//                != PackageManager.PERMISSION_GRANTED
//            ) {
//                return null
//            }
//        }
//        val albumDetail = AlbumDetail()
//        val arrPt: ArrayList<PictureDetail> = ArrayList<PictureDetail>()
//        val projection1: Array<String>
//        projection1 = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
//            arrayOf(
//                MediaStore.Video.Media._ID,
//                MediaStore.Video.Media.DATA, MediaStore.Video.Media.BUCKET_ID,
//                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
//                MediaStore.Video.Media.DATE_TAKEN,
//                MediaStore.Video.Media.WIDTH,
//                MediaStore.Video.Media.DATE_MODIFIED,
//                MediaStore.Video.Media.HEIGHT,
//                MediaStore.Video.Media.SIZE,
//                MediaStore.Video.Media.TITLE,
//                MediaStore.Video.Media.DATE_ADDED,
//                MediaStore.Video.Media.DURATION
//            )
//        } else {
//            arrayOf(
//                MediaStore.Video.Media._ID,
//                MediaStore.Video.Media.RELATIVE_PATH,
//                MediaStore.Video.Media.BUCKET_ID,
//                MediaStore.Video.Media.DISPLAY_NAME,
//                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
//                MediaStore.Video.Media.DATE_MODIFIED,
//                MediaStore.Video.Media.DATE_TAKEN,
//                MediaStore.Video.Media.WIDTH,
//                MediaStore.Video.Media.HEIGHT,
//                MediaStore.Video.Media.SIZE,
//                MediaStore.Video.Media.TITLE,
//                MediaStore.Video.Media.DATE_ADDED,
//                MediaStore.Video.Media.DURATION
//            )
//        }
//        val mImageCursor =
//            mActivity.contentResolver.query(uri!!, projection1, null, null, "datetaken DESC")
//        val calendar = Calendar.getInstance(Locale.US)
//        if (mImageCursor != null && mImageCursor.count > 0) {
//            Timber.e("giangld video %s", mImageCursor.count)
//            if (mImageCursor.moveToFirst()) {
//                do {
//                    val photo = PictureDetail()
//                    var path: String
//                    path = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
//                        mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Video.Media.DATA))
//                    } else {
//                        PATH + mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Video.Media.RELATIVE_PATH)) +
//                                mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
//                    }
//                    path = path.replace("_tmp", "")
//                    photo.setPath(path)
//                    photo.setUriContentResolver(
//                        ContentUris.withAppendedId(
//                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                            mImageCursor.getInt(mImageCursor.getColumnIndex(MediaStore.Video.VideoColumns._ID))
//                                .toLong()
//                        ).toString()
//                    )
//                    val bucket =
//                        mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
//                    val dateLastModified: Long = getDateLastModifiedVideo(mImageCursor)
//                    photo.setBucketId(mImageCursor.getInt(mImageCursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID)))
//                    photo.setBucketName(
//                        bucket ?: mActivity.applicationContext.getString(R.string.all_photo)
//                    )
//                    photo.setDateLastModified(dateLastModified)
//                    setTimeForMedia(photo, dateLastModified, calendar)
//                    photo.setWidth(mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Video.Media.WIDTH)))
//                    photo.setHeight(mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)))
//                    photo.setSize(mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Video.Media.SIZE)))
//                    val title =
//                        mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Video.Media.TITLE))
//                    val duration =
//                        mImageCursor.getLong(mImageCursor.getColumnIndex(MediaStore.Video.Media.DURATION))
//                    if (duration > 0) photo.setDuration(duration)
//                    photo.setTitle(title)
//                    photo.setSelect(false)
//                    photo.setVideo(true)
//                    arrPt.add(photo)
//                } while (mImageCursor.moveToNext())
//            }
//            mImageCursor.close()
//        }
//        albumDetail.setBucketName(mActivity.getString(R.string.videos))
//        albumDetail.setVideo(true)
//        albumDetail.setTotalCountImage(0)
//        albumDetail.setTotalCountVideo(arrPt.size)
//        Timber.e("giangld video size 2 %s", arrPt.size)
//        albumDetail.setTotalCount(albumDetail.getTotalCountVideo())
//        albumDetail.setPictureDetails(arrPt)
//        return albumDetail
//    }
}