package h.chan.ContactPicker

import android.app.Activity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.AdapterView
import android.view.View
import android.content.ContentUris
import android.content.Intent

class ContactPicker extends Activity {

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)

    val cursor = getContentResolver().query(
      ContactsContract.Contacts.CONTENT_URI,
      null, null, null, null)
    val from = Array(ContactsContract.ContactNameColumns.DISPLAY_NAME_PRIMARY)
    val to = Array(R.id.itemTextView)
    val adapter = new SimpleCursorAdapter(this,
      R.layout.listitemlayout, cursor, from, to)
    val listView = findViewById(R.id.contactListView).asInstanceOf[ListView]
    listView.setAdapter(adapter)

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener {
      def onItemClick(parent: AdapterView[_], view: View, pos: Int, id: Long): Unit = {
        cursor.moveToPosition(pos)
        val rowId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
        val outURI = ContentUris.withAppendedId(
          ContactsContract.Contacts.CONTENT_URI, rowId)
        val outData = new Intent()
        outData.setData(outURI)
        setResult(Activity.RESULT_OK, outData)
        finish()
      }
    })
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.contact_picker, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    val id = item.getItemId
    if (id == R.id.action_settings) {
      return true
    }
    super.onOptionsItemSelected(item)
  }
}
