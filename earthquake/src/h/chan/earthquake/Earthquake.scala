package h.chan.earthquake

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.content.Intent
import android.preference.PreferenceManager

class Earthquake extends Activity {

  final val MENU_PREFERENCES = Menu.FIRST + 1;
  final val MENU_UPDATE = Menu.FIRST + 2;
  final val SHOW_PREFERENCES = 1;

  var minimumMagnitude = 0
  var autoUpdateChecked = false
  var updateFreq = 0

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)

    updateFromPreferences()
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    super.onCreateOptionsMenu(menu)
    menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences)
    true
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    super.onOptionsItemSelected(item)
    item.getItemId() match {
      case MENU_PREFERENCES =>
        val intent = new Intent(this, classOf[PreferencesActivity])
        startActivityForResult(intent, SHOW_PREFERENCES)
        true
      case _ =>
        false
    }
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == SHOW_PREFERENCES)
      if (resultCode == Activity.RESULT_OK) {
        updateFromPreferences()
        val fm = getFragmentManager()
        val earthquakeList =
          fm.findFragmentById(R.id.EarthquakeListFragment).asInstanceOf[EarthquakeListFragment]
        (new Thread(new Runnable {
          def run(): Unit = {
            earthquakeList.refreshEarthquakes
          }
        })).start()
      }
  }

  private def updateFromPreferences(): Unit = {
    val context = getApplicationContext()
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    val minMagIndex = prefs.getInt(PreferencesActivity.PREF_MIN_MAG_INDEX, 0)
    val freqIndex = prefs.getInt(PreferencesActivity.PREF_FREQ_INDEX, 0)

    autoUpdateChecked = prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false)

    val r = getResources()
    val minMagValues = r.getStringArray(R.array.magnitude)
    val freqValues = r.getStringArray(R.array.update_freq_values)

    minimumMagnitude = minMagValues(minMagIndex).toInt
    updateFreq = freqValues(freqIndex).toInt
  }
}
