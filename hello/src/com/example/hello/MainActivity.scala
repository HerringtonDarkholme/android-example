package com.example.hello

import java.util.ArrayList
import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.app.Fragment


class MainActivity extends Activity with NewItemListener {

  var todoItems: ArrayList[String] = new ArrayList[String]()
  var aa: ArrayAdapter[String] = _

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val fm = getFragmentManager()
    val todoListFragment =
      fm.findFragmentById(R.id.ToDoListFragment).asInstanceOf[ToDoListFragment]
    aa = new ArrayAdapter(this, R.layout.todolist_item, todoItems)
    todoListFragment.setListAdapter(aa)
  }

  def onNewItemAdded(newItem: String): Unit = {
    todoItems.add(newItem)
    aa.notifyDataSetChanged()
  }

  def find[T <: View](id: Int): T  = {
    findViewById(id).asInstanceOf[T]
  }
}
