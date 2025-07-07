package garcia.carlosdamian.mypokedex


import com.google.firebase.firestore.FirebaseFirestore
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.callback.ErrorInfo
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.EditText
import android.content.Intent
import android.widget.Button
import android.widget.Toast
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.net.Uri

@Suppress("DEPRECATION")
class PokemonRegistryActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_GET = 1
    private val UPLOAD_PRESET = "pokemon-upload"

    private var imageUri: Uri? = null
    private var imagePublicUrl: String? = null

    private lateinit var etPokemonId: EditText
    private lateinit var etPokemonName: EditText
    private lateinit var ivPokemonThumbnail: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnSavePokemon: Button

    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_registry)

        bindViews()
        setupListeners()
    }

    private fun bindViews() {
        etPokemonId = findViewById(R.id.etPokemonId)
        etPokemonName = findViewById(R.id.etPokemonName)
        ivPokemonThumbnail = findViewById(R.id.ivPokemonThumbnail)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnSavePokemon = findViewById(R.id.btnSavePokemon)
    }

    private fun setupListeners() {
        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }

        btnSavePokemon.setOnClickListener {
            if (!validateFields()) return@setOnClickListener
            savePokemon()
        }
    }

    private fun validateFields(): Boolean {
        val idValue = etPokemonId.text.toString().trim()
        val nameValue = etPokemonName.text.toString().trim()

        if (idValue.isEmpty()) {
            Toast.makeText(
                this,
                "Ingrese el ID del Pokémon",
                Toast.LENGTH_SHORT
            ).show()

            return false
        }
        if (nameValue.isEmpty()) {
            Toast.makeText(
                this,
                "Ingrese el nombre del Pokémon",
                Toast.LENGTH_SHORT
            ).show()

            return false
        }

        return true
    }

    private fun savePokemonToFirestore() {
        val idValue = etPokemonId.text.toString().trim()
        val nameValue = etPokemonName.text.toString().trim()

        val pokemon = hashMapOf(
            "id" to idValue,
            "name" to nameValue,
            "imageUrl" to (imagePublicUrl ?: "")
        )

        db.collection("pokemons")
            .add(pokemon)
            .addOnSuccessListener {
                Toast.makeText(this, "Pokemon registrado con exito", Toast.LENGTH_SHORT).show()
                etPokemonId.text.clear()
                etPokemonName.text.clear()
                ivPokemonThumbnail.setImageResource(R.mipmap.ic_pokeball_foreground)
                imageUri = null
                imagePublicUrl = null
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            val fullPhotoUri: Uri? = data?.data
            if (fullPhotoUri != null) {
                ivPokemonThumbnail.setImageURI(fullPhotoUri)
                imageUri = fullPhotoUri
            }
        }
    }

    fun savePokemon(): String {
        if (imageUri == null) {
            Toast.makeText(this, "Seleccione una imagen antes de registrar", Toast.LENGTH_SHORT).show()
            return ""
        }

        val requestId = MediaManager.get().upload(imageUri)
            .unsigned(UPLOAD_PRESET)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    Log.i("Cloudinary", "Upload started")
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    Log.i("Cloudinary", "Uploading: $bytes/$totalBytes")
                }

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    imagePublicUrl = resultData?.get("secure_url") as String?
                    Log.i("Cloudinary", "Upload success: $imagePublicUrl")
                    savePokemonToFirestore()
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Log.e("Cloudinary", "Upload error: ${error?.description}")
                    Toast.makeText(this@PokemonRegistryActivity, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    Log.i("Cloudinary", "Upload rescheduled")
                }
            })
            .dispatch()

        return requestId
    }
}