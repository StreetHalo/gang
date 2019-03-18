package com.release.gang

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_img.*
import kotlin.random.Random
import android.os.AsyncTask
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.card_view_img.*

class ImgActivity : AppCompatActivity() {
    var listImgs = mutableListOf<String>()
    var listQuotes = mutableListOf<String>()
    val db = FirebaseFirestore.getInstance()
    var text = "null"
    var textRandom = 0
    var imgRandom = 0
    var bitmap : Bitmap? = null
    private var listGroup = arrayListOf<Group>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img)
        listGroup = intent.getParcelableArrayListExtra("groupId")
        setImg(listGroup)
        setQuote(listGroup)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener {
        onBackPressed()
        }

        refresh.setOnClickListener {

                 setImg(listGroup)
                    setQuote(listGroup)

        }

        share.setOnClickListener{

            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ==PackageManager.PERMISSION_GRANTED) setIntent()
            else
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),REQUEST_PERMISSIONS_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        if(isConnected())
        else Toast.makeText(this,"Для работы приложения необходимо подключения к Интернету", Toast.LENGTH_LONG).show()
    }


    private fun setImg(listImgs: ArrayList<Group>) {
        imgRandom = Random.nextInt(0, listImgs.size)
        Glide.with(applicationContext)
            .load(listImgs[imgRandom].groupImg)
            .into(img)

    }

    private fun isConnected(): Boolean {
        val connectManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }


    private fun setQuote(listQuote: ArrayList<Group>) {


        textRandom = Random.nextInt(0, listQuote.size)
        text = listQuote[textRandom].text
        quote.text =text
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        setIntent()
                } else {
                }
            }
        }
    }

    private fun setIntent(){

        Async().execute()

    }

    @SuppressLint("StaticFieldLeak")
    inner class Async : AsyncTask<Void,Void,Void>(){

        override fun onPostExecute(result: Void?) {
            val path = MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap, "Design", null
            )
            val uri = Uri.parse(path)

            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, text)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "image/jpg"
            startActivity(Intent.createChooser(intent,"Поделись"))
            super.onPostExecute(result)
            cancel(true)
        }

        override fun doInBackground(vararg params: Void?): Void? {
             bitmap = Glide.with(applicationContext)
                .asBitmap()
                .load(listGroup[imgRandom].groupImg)
                .submit()
                .get()
            return null
        }
    }
}




