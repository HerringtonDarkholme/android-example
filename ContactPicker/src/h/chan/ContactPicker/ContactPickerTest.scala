package h.chan.ContactPicker

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView

class ContactPickerTester extends Activity {

  val PICK_CONTACT = 1

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.contactpickertester)

    val button = findViewById(R.id.pick_contact_button)
    button.setOnClickListener(new OnClickListener {
      override def onClick(_view: View): Unit = {
        val intent = new Intent(Intent.ACTION_PICK,
          Uri.parse("content://contacts/")
        )
        startActivityForResult(intent, PICK_CONTACT)
      }
    })
  }

  override def onActivityResult(reqCode: Int, resCode: Int, data: Intent): Unit = {
    if (reqCode != PICK_CONTACT) return
    if (resCode != Activity.RESULT_OK) return

    val contactData: Uri = data.getData()
    val cursor: Cursor = getContentResolver().query(
      contactData, null, null, null, null
    )
    cursor.moveToFirst()
    val name = cursor.getString(cursor.getColumnIndexOrThrow(
      ContactsContract.ContactNameColumns.DISPLAY_NAME_PRIMARY
    ))
    cursor.close()
    val textView = findViewById(R.id.selected_contact_textview).asInstanceOf[TextView]
    textView.setText(name)
  }
}
