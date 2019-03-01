package com.example.imggenerator

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.card_view_img.*

class ImgActivity : AppCompatActivity() {
    var listImgs = mutableListOf<String>()
    var listQuotes = mutableListOf<String>()
    val db = FirebaseFirestore.getInstance()

    var textRandom = 0
    var imgRandom = 0
    var bitmap : Bitmap? = null


   private val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img)
        val groupName = intent.getStringExtra("groupId")
        setArrays(groupName)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener {
        onBackPressed()
        }

        refresh.setOnClickListener {

                if(listImgs.size>0) setImg(listImgs)
                if(listQuotes.size>0) setQuote(listQuotes)


        }

        share.setOnClickListener{

            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ==PackageManager.PERMISSION_GRANTED) setIntent()
            else
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),REQUEST_PERMISSIONS_CODE)
        }
    }


   private fun setArrays(groupName:String){

            getInfo(groupName)

    }

    private fun setImg(listImgs: MutableList<String>) {
        imgRandom = Random.nextInt(0, listImgs.size)
        Glide.with(this)
            .load(listImgs[imgRandom])
            .into(img)

    }

    private fun getInfo(groupName:String){

        db.collection(groupName)
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    listImgs.add(document.data["img"] as String)
                    listQuotes.add(document.data["quote"] as String)

                }
                setQuote(listQuotes)
                setImg(listImgs)
            }
            .addOnCanceledListener {  }
    }



    private fun setQuote(listQuote: MutableList<String>) {
        textRandom = Random.nextInt(0, listQuote.size)
         quote.text =listQuote[textRandom]
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
            intent.putExtra(Intent.EXTRA_TEXT, listQuotes[textRandom])
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "image/jpg"
            startActivity(Intent.createChooser(intent,"Поделись"))
            super.onPostExecute(result)
            cancel(true)
        }

        override fun doInBackground(vararg params: Void?): Void? {
             bitmap = Glide.with(baseContext)
                .asBitmap()
                .load(listImgs[imgRandom])
                .submit()
                .get()
            return null
        }
    }
}




