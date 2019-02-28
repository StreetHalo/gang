package com.example.imggenerator

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),
    DiscreteScrollView.ScrollListener<GalleryAdapter.ViewHolder>,
    DiscreteScrollView.OnItemChangedListener<GalleryAdapter.ViewHolder>,
    ViewInterface{
    val TAG = this.javaClass.simpleName
    override fun onCurrentItemChanged(viewHolder: GalleryAdapter.ViewHolder?, adapterPosition: Int) {
        Log.d("Activity","position $adapterPosition")
        progressBar.visibility = View.INVISIBLE
        about.text = listGroup[adapterPosition].groupName
        about.visibility = View.VISIBLE
        picker.visibility = View.VISIBLE

    }

    val db = FirebaseFirestore.getInstance()
    val listGroup = arrayListOf<Group>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getGroupList()

        picker.addOnItemChangedListener(this)
        picker.setItemTransformer(
            ScaleTransformer.Builder()

                .setMinScale(0.8f)
                .build()
        )
    }
    override fun setGroup(idGroup: Int) {

        val intent = Intent(this,ImgActivity::class.java)
        intent.putExtra("groupId", listGroup[idGroup].groupId)
        startActivity(intent)
    }



    private fun getGroupList() {

        db.collection("group")
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    listGroup.add(Group(document.data["groupName"] as String,document.data["groupId"] as String ,document.data["groupImg"] as String))
                }
                picker.adapter = GalleryAdapter(listGroup,this)
            }
    }

    override fun onScroll(
        scrollPosition: Float,
        currentPosition: Int,
        newPosition: Int,
        currentHolder: GalleryAdapter.ViewHolder?,
        newCurrent: GalleryAdapter.ViewHolder?
    ) {
        if (currentHolder != null && newCurrent != null) {
            val position = Math.abs(currentPosition)
            Log.d("Activity", "pos $position")
        }
    }

    private fun saveInSharedPreference(){


    }
}


