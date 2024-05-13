package com.example.texttospeech.main

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.texttospeech.R

import com.example.texttospeech.databinding.ActivityReadEpubBinding
import com.example.texttospeech.library.component.EpubReaderComponent
import com.example.texttospeech.library.entity.BookEntity
import com.example.texttospeech.library.entity.FontEntity
import com.example.texttospeech.library.entity.SubBookEntity
import com.example.texttospeech.library.view.OnHrefClickListener
import com.example.texttospeech.main.adapter.BookAdapter
import com.example.texttospeech.main.adapter.SubBookAdapter
import com.example.texttospeech.main.bottomsheet.OnChangeFontFamily
import com.example.texttospeech.main.bottomsheet.OnChangeFontSize
import com.example.texttospeech.main.bottomsheet.ToolsBottomSheet
import com.example.texttospeech.main.view.MyRecycler
import java.io.File


class ReadEpub : AppCompatActivity(), View.OnClickListener {
    private val listFont: ArrayList<FontEntity> = ArrayList()
    private var epubReader: EpubReaderComponent? = null
    private var adapter: BookAdapter? = null

    private var rvBook: MyRecycler? = null
    private var drawer: DrawerLayout? = null
    private var drawerLayout: View? = null
    private var toolbar: Toolbar? = null
    private var rvSubList: RecyclerView? = null
    private var tvBookName: TextView? = null
    private var tvBookAuthor: TextView? = null
    private var ivBook: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_epub)
        bindView()

        setSupportActionBar(toolbar)
        val getFilePath = intent.getStringExtra("file_path")?:""
        onBookReady(getFilePath)
        listFont.add(
            FontEntity(
                "https://hamedtaherpour.github.io/sample-assets/font/Acme.css",
                "Acme"
            )
        )
        listFont.add(
            FontEntity(
                "https://hamedtaherpour.github.io/sample-assets/font/IndieFlower.css",
                "IndieFlower"
            )
        )
        listFont.add(
            FontEntity(
                "https://hamedtaherpour.github.io/sample-assets/font/SansitaSwashed.css",
                "SansitaSwashed"
            )
        )

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_menu -> toggleDrawer()
            R.id.btn_setting -> openToolsMenu()
        }
    }

    private fun bindView() {
        rvBook = findViewById(R.id.rv_book)
        drawer = findViewById(R.id.drawer)
        drawerLayout = findViewById(R.id.l_drawer)
        rvSubList = findViewById(R.id.rv)
        toolbar = findViewById(R.id.tool_bar)
        ivBook = findViewById(R.id.iv_book)
        tvBookName = findViewById(R.id.tv_book_name)
        tvBookAuthor = findViewById(R.id.tv_book_author)
        findViewById<View>(R.id.btn_menu).setOnClickListener(this)
        findViewById<View>(R.id.btn_setting).setOnClickListener(this)
    }
    private fun onBookReady(filePath: String) {
        try {
            epubReader = EpubReaderComponent(filePath)
            val bookEntity: BookEntity = epubReader!!.make(this)
            Log.d("faiz cek", bookEntity.toString())
            setUpBookAdapter(bookEntity.getPagePathList())
            setUpBookInfo(bookEntity.getName(), bookEntity.getAuthor(), bookEntity.getCoverImage())
            setUpBookSubList(bookEntity.getSubBookHref())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    private fun openToolsMenu() {
        val sheet = ToolsBottomSheet()
        sheet.setFontSize(adapter!!.getFontSize())
        sheet.setAllFontFamily(listFont)
        sheet.setOnChangeFontFamily(object : OnChangeFontFamily {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChange(position: Int) {
                adapter!!.setFontEntity(listFont[position])
                adapter!!.notifyDataSetChanged()
            }
        })

        sheet.setOnChangeFontSize(object : OnChangeFontSize {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChangeSize(size: Int) {
                adapter!!.setFontSize(size)
                adapter!!.notifyDataSetChanged()
            }

        })
        sheet.show(supportFragmentManager, sheet.getTag())
    }

    private fun setUpBookAdapter(list: List<String>) {
        adapter = BookAdapter(list, epubReader!!.absolutePath)
        val layoutManager: LinearLayoutManager =
            object : LinearLayoutManager(this, HORIZONTAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
        rvBook!!.layoutManager = layoutManager
        rvBook!!.adapter = adapter
        adapter!!.setFontSize(30)
        adapter!!.setOnHrefClickListener(object :OnHrefClickListener{
            override fun onClick(href: String?) {
                gotoPageByHref(href)
            }

        })
        LinearSnapHelper().attachToRecyclerView(rvBook)
    }
    private fun setUpBookInfo(name: String, author: String, filePathImgCover: String?) {
        tvBookName!!.text = name
        tvBookAuthor!!.text = author
        if (filePathImgCover != null) ivBook!!.setImageBitmap(fileToBitmap(File(filePathImgCover)))
    }

    private fun setUpBookSubList(list: List<SubBookEntity>) {
        val adapter = SubBookAdapter()
        adapter.setOnItemClickListener { view, entity, position ->
            gotoPageByHref(entity!!.getHref())
            drawer!!.closeDrawer(drawerLayout!!)
        }
        rvSubList!!.addItemDecoration(
            DividerItemDecoration(
                applicationContext,
                DividerItemDecoration.VERTICAL
            )
        )
        rvSubList!!.adapter = adapter
        adapter.submitList(list)
    }
    fun gotoPageByHref(href: String?) {
        val position = epubReader!!.getPagePositionByHref(href)
        if (position != EpubReaderComponent.PAGE_NOT_FOUND) rvBook!!.scrollToPosition(position)
    }

    private fun toggleDrawer() {
        if (drawer!!.isDrawerOpen(drawerLayout!!)) drawer!!.closeDrawer(drawerLayout!!) else {
            drawer!!.openDrawer(drawerLayout!!)
        }
    }

    private fun fileToBitmap(file: File): Bitmap? {
        return if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else null
    }

    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(drawerLayout!!)) drawer!!.closeDrawer(drawerLayout!!) else super.onBackPressed()
    }
}