package garcia.carlosdamian.mypokedex

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.cloudinary.android.MediaManager
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.initialize

class App : Application() {

    val CLOUD_NAME = "dzmp3qmgm"
    val REQUEST_IMAGE_GET = 1
    val UPLOAD_PRESET = "pokemon-upload"

    override fun onCreate() {
        super.onCreate()

        try {
            val config = HashMap<String, String>()
            config["cloud_name"] = CLOUD_NAME
            MediaManager.init(this, config)

            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            e.message?.let { Log.w("App", it) }

            Toast.makeText(
                this,
                "Ocurrio un error al conectarse con Cloudinary o Firebase...",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}