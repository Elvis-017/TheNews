package com.example.thenews

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thenews.databinding.ActivityMainBinding
import com.example.thenews.model.INewsService
import com.example.thenews.model.NewsModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {


    lateinit var setCategory: String
    var  setSize: Int = 20
    lateinit var   setQ: String

     var   dataChangeEndpoint: Int? = 0

    private lateinit var binding: ActivityMainBinding

    val error= MutableLiveData<String>()
    val newsModel= MutableLiveData<NewsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_main)

         binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.bottomNavigationbar.setOnNavigationItemSelectedListener (this)

        setCategory = "general"
        setQ = ""

        binding.btnSearch.setOnClickListener{
            newsEverything(binding.txtSearch.text.toString(), setSize)
        }

        newsTopHeadlines(setCategory,setSize)

        binding.btnPageSize.setOnClickListener{
            setSize += 10

            if(dataChangeEndpoint == 0) {
                newsTopHeadlines(setCategory, setSize)
            }else{
                newsEverything(binding.txtSearch.text.toString(), setSize)
           }

        }

        binding.recycleView.layoutManager = LinearLayoutManager(this)
         newsModel.observe(this, {obj ->
             binding.recycleView.adapter = NewsAdapter(obj)
         })

        error.observe(this, {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        })
    }

    fun newsEverything( query: String, size: Int){
        dataChangeEndpoint = 1
        INewsService.getClient().getNewsEverything(query, size ).enqueue(object: Callback<NewsModel>{
            override fun onResponse(call: Call<NewsModel>, response: Response<NewsModel>) {
                try{
                    val responseBody = response.body()
                    newsModel.postValue(responseBody)

                }catch (e: Exception){
                    failRquestMessage(e.message)
                }
            }

            override fun onFailure(call: Call<NewsModel>, t: Throwable) {
                error.postValue(t.message)

                failRquestMessage(t.message)
            }
        })
    }

    fun newsTopHeadlines(category: String,  size: Int){
        dataChangeEndpoint = 0
        INewsService.getClient().getNewsTopHeadlines(category,  size ).enqueue( object: Callback<NewsModel> {
            override fun onResponse(call: Call<NewsModel>, response: Response<NewsModel>) {

                try{
                    val responseBody = response.body()
                    newsModel.postValue(responseBody)

                }catch (e: Exception){
                    failRquestMessage(e.message)
                }
            }

            override fun onFailure(call: Call<NewsModel>, t: Throwable) {
                error.postValue(t.message)
                failRquestMessage(t.message)
            }
        })
    }

    fun failRquestMessage(message: String?){

        val alertBuilder =  AlertDialog.Builder(this@MainActivity)
        alertBuilder.setTitle("error en respuesra")
        alertBuilder.setMessage(message.toString())
        alertBuilder.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        setSize = 20
        binding.txtSearch.text.clear()
        when(item.itemId ){
            R.id.itemEntertainment->{
                setCategory = "entertainment"
                newsTopHeadlines(setCategory ,setSize)
            }
            R.id.itemGeneral->{
                setCategory = "general"
                newsTopHeadlines(setCategory ,setSize)
            }
            R.id.itemSports->{
                setCategory = "sports"
                newsTopHeadlines(setCategory ,setSize)
            }
            R.id.itemHealth->{
                setCategory = "health"
                newsTopHeadlines(setCategory ,setSize)
            }
            R.id.itemTechnology->{
                setCategory = "technology"
                newsTopHeadlines(setCategory ,setSize)
            }
            else-> false
        }
        return true
    }
}

class NewsAdapter(private val items: NewsModel?): RecyclerView.Adapter<NewsAdapter.ViewHolder>(){

    class ViewHolder(item: View): RecyclerView.ViewHolder(item) {
        val title = item.findViewById<TextView>(R.id.txtTitle)
        val author = item.findViewById<TextView>(R.id.txtAuthor)
        val urlToImage = item.findViewById<ImageView>(R.id.newsImage)
        val url = item.findViewById<TextView>(R.id.txtUrl)
        val date = item.findViewById<TextView>(R.id.txtDate)
        val description= item.findViewById<TextView>(R.id.txtDescription)
        val webView = item.findViewById<WebView>(R.id.webView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items?.articles?.count()!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var items = items?.articles?.get(position)

        holder.title.text = items?.title
        holder.author.text = items?.author
        holder.date.text = items?.date.toString().substring(0, 10)
        holder.description.text = items?.description

        Picasso.get().load(items?.image).error(R.drawable.resource_default).into(holder.urlToImage)

        holder.url.setOnClickListener {
            holder.webView.visibility = View.VISIBLE
            holder. webView.loadUrl(items?.url.toString())
            holder.url.visibility = View.INVISIBLE
        }

    }
}
