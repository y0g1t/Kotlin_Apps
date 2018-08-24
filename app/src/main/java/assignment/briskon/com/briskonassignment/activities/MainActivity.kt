package assignment.briskon.com.briskonassignment.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.*
import assignment.briskon.com.briskonassignment.Network.ApiClient
import assignment.briskon.com.briskonassignment.Network.ApiInterface
import assignment.briskon.com.briskonassignment.R
import assignment.briskon.com.briskonassignment.adapter.StateAdapter
import assignment.briskon.com.briskonassignment.model.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val TAG: String = "MainActivity"
    private val SELECT_COUNTRY_NAME: String = "Select Country"
    private val MY_PERMISSIONS_REQUEST_CAMERA = 123

    private val GALLERY = 1
    private val CAMERA = 2

    private var mCountryList: ArrayList<CountryFeed>? = null
    private var mStateList: ArrayList<StateFeed>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        click_pic!!.setOnClickListener { showPictureDialog() }

        // Check & ask to permission
        askPermission()
        //Call Api to get country data
        apiCall()

    }


    private fun apiCall() {

        val service = ApiClient.apiClient?.create(ApiInterface::class.java)
        val call = service?.getAllCountry()
        call?.enqueue(object : Callback<ApiResponse> {

            override fun onResponse(call: Call<ApiResponse>?, response: Response<ApiResponse>?) {
                val body = response?.body()
                mCountryList = body?.restResponse?.countryList
                //val  countryCount= mCountryList?.size

                //Display countries name in spinner
                setSpinnerData()
            }


            override fun onFailure(call: Call<ApiResponse>?, t: Throwable?) {
                Toast.makeText(this@MainActivity, "error reading JSON", Toast.LENGTH_LONG)
            }
        })

    }

    private fun setSpinnerData() {

        var countryName: ArrayList<String> = ArrayList()

        countryName?.add(SELECT_COUNTRY_NAME)

        // Filter country name
        for (i in 0..mCountryList?.size!! - 1 step 1) {
            //println(i)
            countryName?.add(mCountryList?.get(i)?.name!!.toString())
        }

        // Initializing an ArrayAdapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countryName)

        // Set the drop down view resource
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        // Finally, data bind the spinner object with dapter
        spinner.adapter = adapter;

        // Set an on item selected listener for spinner object
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Display the selected item text
                println("Spinner selected : ${parent.getItemAtPosition(position).toString()}")
                var countryCode: String? = null

                //Filter alpha3_code of selected item from list
                for (i in 0..mCountryList?.size!! - 1 step 1) {
                    if (mCountryList?.get(i)?.name == parent.getItemAtPosition(position).toString()) {
                        countryCode = mCountryList?.get(i)?.alpha3_code
                        break
                    }
                }
                if (countryCode != null && !countryCode.contentEquals(SELECT_COUNTRY_NAME)) {

                    // Api call for geting state acording to selected country
                    apiCallForStateList(countryCode)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }
    }

    // Api call for geting state acording to selected country
    private fun apiCallForStateList(countryCode: String) {

        val service = ApiClient.apiClient?.create(ApiInterface::class.java)
        val call = service?.getAllState(countryCode)
        call?.enqueue(object : Callback<StateApiResponse> {

            override fun onResponse(call: Call<StateApiResponse>?, response: Response<StateApiResponse>?) {
                val body = response?.body()
                mStateList = body?.stateListResponse?.stateList
                val  stateCount= mStateList?.size
                println(stateCount)

                if (stateCount != 0){
                    adapterLoad(mStateList)
                }else {
                    Toast.makeText(this@MainActivity, "There is no states", Toast.LENGTH_LONG).show()

                }

            }


            override fun onFailure(call: Call<StateApiResponse>?, t: Throwable?) {
                Toast.makeText(this@MainActivity, "error reading JSON", Toast.LENGTH_LONG).show()
            }
        })

    }

    // Load state , state capital in a item , call adapter
    private fun adapterLoad(mStateList: ArrayList<StateFeed>?) {
        // Creates a vertical Layout Manager
        country_state_list.layoutManager = LinearLayoutManager(this@MainActivity)
        // Access the RecyclerView Adapter and load the data into it
        country_state_list.adapter = StateAdapter(mStateList!!, this@MainActivity)
    }


    private fun askPermission(){

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA),
                        MY_PERMISSIONS_REQUEST_CAMERA)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    // Create Alart Dailog to choose option for image load
    private fun showPictureDialog() {
        val pictureDialog = android.app.AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    // Choose image from gallary
    private fun choosePhotoFromGallary() {
        //askPermission()
        val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    // Take image from camera
    private fun takePhotoFromCamera() {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA)
        }catch (e1:Exception){
            Toast.makeText(this@MainActivity, "Please give Camera permission", Toast.LENGTH_SHORT).show()

            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA),
                    MY_PERMISSIONS_REQUEST_CAMERA)
            e1.printStackTrace()

        }

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    //val path = saveImage(bitmap)
                    Toast.makeText(this@MainActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
                    user_image!!.setImageBitmap(bitmap)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == CAMERA) {
            if (data != null) {
                try {
                    val image = data.extras!!.get("data") as Bitmap
                    user_image!!.setImageBitmap(image)
                    saveImage(image)
                    Toast.makeText(this@MainActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
                } catch (e1: NullPointerException) {
                    e1.printStackTrace()
                }
            }
        }
    }

    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 99, bytes)
        val wallpaperDirectory = File(
                (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d(TAG, wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {
            //Create new diectory
            wallpaperDirectory.mkdirs()
        }

        try {
            Log.d(TAG, wallpaperDirectory.toString())
            //
            val file = File(wallpaperDirectory, ((Calendar.getInstance().getTimeInMillis()).toString() + ".jpg"))
            file.createNewFile()
            val fo = FileOutputStream(file)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,arrayOf(file.getPath()),arrayOf("image/jpeg"), null)
            fo.close()
            Log.d(TAG, "File Saved ::" + file.getAbsolutePath())

            return file.getAbsolutePath()

        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"
    }


}
