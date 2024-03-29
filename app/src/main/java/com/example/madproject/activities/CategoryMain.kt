package com.example.madproject.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madproject.R
import com.example.madproject.activities.CreateCategories
import com.example.madproject.activities.DeleteCategory
import com.example.madproject.activities.UpdateCategory
import com.example.madproject.adapteres.CategoryAdapter
import com.example.madproject.models.CategoryModel
import com.google.firebase.database.*

class CategoryMain : AppCompatActivity() {

    private lateinit var empRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var empList: ArrayList<CategoryModel>
    private lateinit var dbRef: DatabaseReference
    private lateinit var addBtn: Button



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.job_category_user_view)

        empRecyclerView = findViewById(R.id.rvCategory)
        empRecyclerView.layoutManager = LinearLayoutManager(this)
        empRecyclerView.setHasFixedSize(true)
        tvLoadingData = findViewById(R.id.tvLoadingData)
        addBtn = findViewById(R.id.addCategoryBtn)

        addBtn.setOnClickListener{
            val intent = Intent(this@CategoryMain, CreateCategories::class.java)
            startActivity(intent)
        }

        empList = arrayListOf<CategoryModel>()

        getEmployeesData()

    }

    private fun getEmployeesData() {

        empRecyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("category")
        Log.d("TAG", "ccccc $dbRef")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                empList.clear()
                if (snapshot.exists()){
                    for (empSnap in snapshot.children){
                        Log.d("TAG", "snap $snapshot")
                        val empData = empSnap.getValue(CategoryModel::class.java)
                        empList.add(empData!!)
                    }
                    val mAdapter = CategoryAdapter(empList)
                    empRecyclerView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : CategoryAdapter.onItemClickListener{
                        override fun onItemClick(position: Int, mode : String) {

                            if (mode == "EDT"){
                                val intent = Intent(this@CategoryMain, UpdateCategory::class.java)

                                //put extras
                                intent.putExtra("categoryID", empList[position].categoryId)
                                intent.putExtra("categoryName", empList[position].categoryName)
                                startActivity(intent)
                            }else{
                                val intent = Intent(this@CategoryMain, DeleteCategory::class.java)

                                //put extras
                                intent.putExtra("categoryID", empList[position].categoryId)
                                intent.putExtra("categoryName", empList[position].categoryName)
                                startActivity(intent)
                            }

                        }

                    })

                    empRecyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
