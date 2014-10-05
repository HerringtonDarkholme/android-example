package com.example.hello

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

trait NewItemListener {
  def onNewItemAdded(newItem: String): Unit
}

class NewItemFragment extends Fragment {

  var onNewItemAddedListener: NewItemListener = _

  override def onAttach(activity: Activity): Unit = {
    super.onAttach(activity)
    try {
      activity match {
        case a: NewItemListener =>
         onNewItemAddedListener = a
      }
    } catch {
      case _: MatchError =>
    }
  }

  override def onCreateView(inflater: LayoutInflater,
    container: ViewGroup, savedInstanceState: Bundle
  ): View = {
    val view = inflater.inflate(R.layout.new_item_fragment, container, false)
    val myEditText = view.findViewById(R.id.myEditText).asInstanceOf[EditText]
    myEditText.setOnKeyListener(new View.OnKeyListener() {

      override def onKey(v: View, keyCode: Int, event: KeyEvent): Boolean = {
        if (event.getAction == KeyEvent.ACTION_DOWN) {
          if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            val newItem = myEditText.getText.toString
            onNewItemAddedListener.onNewItemAdded(newItem)
            myEditText.setText("")
            return true
          }
        }
        false
      }
    })
    return view
  }
}

