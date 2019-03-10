package com.example.imggenerator

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.activity_main.*
import com.google.gson.Gson
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import java.io.*


class MainActivity : AppCompatActivity(),
    DiscreteScrollView.ScrollListener<GalleryAdapter.ViewHolder>,
    DiscreteScrollView.OnItemChangedListener<GalleryAdapter.ViewHolder>,
    ViewInterface{
    private val localFile = File.createTempFile("generator", "json")

    val TAG = this.javaClass.simpleName
    var listGroup = ArrayList<Group>()
    var listFiles = ArrayList<Group>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       val mAuth = FirebaseAuth.getInstance();
        if(isConnected())
            getGroupList()
        else Toast.makeText(this,"Для работы приложения необходимо подключения к Интернету",Toast.LENGTH_LONG).show()


        picker.addOnItemChangedListener(this)
        picker.setItemTransformer(
            ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build()
        )
    }


    override fun setGroup(idGroup: Int) {
        Log.d(TAG,listGroup[idGroup].groupId )
        Log.d(TAG, listFiles.size.toString())

        val listToActivity = listFiles.filter {  it.groupId==listGroup[idGroup].text     } as ArrayList<Group>

        val intent = Intent(this,ImgActivity::class.java)
        intent.putExtra("groupId", listToActivity)
        startActivity(intent)
    }

    private fun getGroupList() {
         val storage = FirebaseStorage.getInstance()

         val storageRef = storage.reference

         val pathReference = storageRef.child(JSON_NAME)

        pathReference.getFile(localFile).addOnSuccessListener(object : OnSuccessListener<FileDownloadTask.TaskSnapshot>{
            override fun onSuccess(p0: FileDownloadTask.TaskSnapshot?) {
                val reader = JsonReader(FileReader(localFile))
                reader.isLenient = true
                Log.d(TAG, "ERROR ${localFile.readLines()}")
                val turnsType = object : TypeToken<ArrayList<Group>>() {}.type
                listFiles = Gson().fromJson<ArrayList<Group>>(reader, turnsType)
                listGroup = listFiles.filter {  it.groupId=="group" } as ArrayList<Group>
                picker.adapter = GalleryAdapter(listGroup,this@MainActivity)
            }
        })

        pathReference.getFile(localFile).addOnCanceledListener { OnCanceledListener {
            Toast.makeText(this,"Повторите попытку позже",Toast.LENGTH_LONG).show()
        } }
    }


    override fun onScroll(
        scrollPosition: Float,
        currentPosition: Int,
        newPosition: Int,
        currentHolder: GalleryAdapter.ViewHolder?,
        newCurrent: GalleryAdapter.ViewHolder?
    ) {
        if (currentHolder != null && newCurrent != null) {
        }
    }

    override fun onCurrentItemChanged(viewHolder: GalleryAdapter.ViewHolder?, adapterPosition: Int) {
        progressBar.visibility = View.INVISIBLE
        about.text = listGroup[adapterPosition].text
        about.visibility = View.VISIBLE
        picker.visibility = View.VISIBLE
    }

    private fun isConnected(): Boolean {
        val connectManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }
}


