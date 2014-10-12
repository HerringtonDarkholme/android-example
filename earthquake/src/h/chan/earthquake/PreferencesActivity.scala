package h.chan.earthquake

import android.app.Activity
import android.content.{Context, SharedPreferences}
import SharedPreferences.Editor
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.{ArrayAdapter, Button, CheckBox, Spinner}

class PreferencesActivity extends Activity {
  var autoUpdate: CheckBox = _
  var updateFreqSpinner: Spinner = _
  var magnitudeSpinner: Spinner = _
  var prefs: SharedPreferences = _

  import PreferencesActivity._

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.preferences)

    updateFreqSpinner = find(R.id.spinner_update_freq)
    magnitudeSpinner  = find(R.id.spinner_quake_mag)
    autoUpdate        = find(R.id.checkbox_auto_update)

    populateSpinners()

    val context = getApplicationContext()
    prefs = PreferenceManager.getDefaultSharedPreferences(context)
    updateUIFromPreferences()

    val okButton: Button = find(R.id.okButton)
    okButton.setOnClickListener(new View.OnClickListener {
      def onClick(view: View): Unit = {
        savePreferences()
        PreferencesActivity.this.setResult(Activity.RESULT_OK)
        finish()
      }
    })

    val cancelButton: Button = find(R.id.cancelButton)
    cancelButton.setOnClickListener(new View.OnClickListener {
      def onClick(view: View): Unit = {
        PreferencesActivity.this.setResult(Activity.RESULT_CANCELED)
        finish()
      }
    })
  }

  private def find[V <: View](id: Int): V =
    findViewById(id).asInstanceOf[V]

  private def populateSpinners(): Unit = {
    val fAdapter = ArrayAdapter.createFromResource(
      this,
      R.array.update_freq_options,
      android.R.layout.simple_spinner_item)
    val spinner_dd_item = android.R.layout.simple_spinner_dropdown_item
    fAdapter.setDropDownViewResource(spinner_dd_item)
    updateFreqSpinner.setAdapter(fAdapter)

    val mAdapter = ArrayAdapter.createFromResource(
      this,
      R.array.magnitude_options,
      android.R.layout.simple_spinner_item
    )
    mAdapter.setDropDownViewResource(spinner_dd_item)
    magnitudeSpinner.setAdapter(mAdapter)
  }

  private def updateUIFromPreferences(): Unit = {
    val autoUpChecked = prefs.getBoolean(PREF_AUTO_UPDATE, false)
    val updateFreqIndex = prefs.getInt(PREF_FREQ_INDEX, 2)
    val minMagIndex = prefs.getInt(PREF_MIN_MAG_INDEX, 0)

    updateFreqSpinner.setSelection(updateFreqIndex)
    magnitudeSpinner.setSelection(minMagIndex)
    autoUpdate.setChecked(autoUpChecked)
  }

  private def savePreferences(): Unit = {
    val updateIndex = updateFreqSpinner.getSelectedItemPosition()
    val minMagIndex = magnitudeSpinner.getSelectedItemPosition()
    val autoUpChecked = autoUpdate.isChecked()

    val editor: Editor = prefs.edit()
    editor.putBoolean(PREF_AUTO_UPDATE, autoUpChecked)
    editor.putInt(PREF_FREQ_INDEX, updateIndex)
    editor.putInt(PREF_MIN_MAG_INDEX, minMagIndex)
    editor.commit()
  }
}

object PreferencesActivity {
  final val USER_PREFERENCE    = "USER_PREFERENCE"
  final val PREF_AUTO_UPDATE   = "PREF_AUTO_UPDATE"
  final val PREF_MIN_MAG_INDEX = "PREF_MIN_MAG_INDEX"
  final val PREF_FREQ_INDEX    = "PREF_FREQ_INDEX"
}
