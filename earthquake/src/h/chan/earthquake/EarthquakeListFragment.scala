package h.chan.earthquake

import android.app.ListFragment
import android.widget.ArrayAdapter
import java.util.ArrayList
import android.os.Bundle
import android.os.Handler
import java.net.URL
import java.net.HttpURLConnection
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.NodeList
import org.w3c.dom.Element
import java.text.ParseException
import android.location.Location
import android.util.Log
import java.util.GregorianCalendar
import java.text.SimpleDateFormat
import java.net.MalformedURLException
import javax.xml.parsers.ParserConfigurationException
import org.xml.sax.SAXException
import java.io.IOException
import java.lang.Thread

class EarthquakeListFragment extends ListFragment {

  var aa: ArrayAdapter[Quake] = _
  var earthquakes = new ArrayList[Quake]

  override def onActivityCreated(savedInstanceState: Bundle): Unit = {
    super.onActivityCreated(savedInstanceState)

    val layoutId = android.R.layout.simple_list_item_1

    aa = new ArrayAdapter(getActivity(), layoutId, earthquakes)
    setListAdapter(aa)

    val t: Thread = new Thread(new Runnable {
      def run: Unit =
        refreshEarthquakes
    })
    t.start
  }

  private final val TAG = "EARTHQUAKE"
  private val handler = new Handler()

  def refreshEarthquakes: Unit = {
    var url: URL = null
    try {
      val quakeFeed = getString(R.string.quake_feed)
      url = new URL(quakeFeed)

      val connection =
        url.openConnection().asInstanceOf[HttpURLConnection]

      val responseCode = connection.getResponseCode()
      if (responseCode == HttpURLConnection.HTTP_OK) {
        val in = connection.getInputStream()
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()

        // parse the earthquake feed
        val dom = db.parse(in)
        val docEle = dom.getDocumentElement()

        earthquakes.clear()

        var nl: NodeList = docEle.getElementsByTagName("entry")
        if (nl != null && nl.getLength() > 0) {
          (0 until nl.getLength()) foreach { i =>
            val entry = nl.item(i).asInstanceOf[Element]
            val title =
              entry.getElementsByTagName("title").item(0).asInstanceOf[Element]
            val g =
              entry.getElementsByTagName("georss:point").item(0).asInstanceOf[Element]
            val when =
              entry.getElementsByTagName("updated").item(0).asInstanceOf[Element]
            val link =
              entry.getElementsByTagName("link").item(0).asInstanceOf[Element]

            var details: String = title.getFirstChild.getNodeValue
            val linkString = link.getAttribute("href")

            val point: String = g.getFirstChild.getNodeValue
            val dt: String = when.getFirstChild.getNodeValue
            val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")
            var qdate = new GregorianCalendar(0, 0, 0).getTime()

            try {
              qdate = sdf.parse(dt)
            } catch {
              case e: ParseException => Log.d(TAG, "Date parse exception")
            }

            val location: Array[String] = point.split(" ")
            val l: Location = new Location("dummyGPS")
            l.setLatitude(location(0).toDouble)
            l.setLongitude(location(1).toDouble)

            val magnitudeString = details.split(" ")(1)
            val end = magnitudeString.length - 1
            val magnitude = magnitudeString.substring(0, end).toDouble

            details =
              if (!details.contains(',')) details
              else details.split(",")(1).trim

            val quake = Quake(qdate, details, l, magnitude, linkString)

            handler.post(new Runnable {
              def run: Unit = {
                addNewQuake(quake)
              }
            })
          }
        }
      }
    } catch {
      case e: MalformedURLException =>
        Log.d(TAG, "Malformed url")
      case e: IOException =>
        Log.d(TAG, "Bad internet")
      case e: SAXException =>
        Log.d(TAG, "Bad XML")
      case e: ParserConfigurationException =>
        Log.d(TAG, "Parser go wrong")
    }
  }

  def addNewQuake(quake: Quake): Unit = {
    val earthquakeActivity = getActivity().asInstanceOf[Earthquake]
    if (quake.magnitude > earthquakeActivity.minimumMagnitude) {
      earthquakes.add(quake)
      aa.notifyDataSetChanged()
    }
  }
}
