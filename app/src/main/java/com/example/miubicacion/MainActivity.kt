package com.example.miubicacion

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.Transformations.map

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.miubicacion.databinding.ActivityMapsBinding
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val PERMISO_LOCALIZACION: Int = 3
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = true

        //Comprobando los permisos

        enableMyLocation()

        //Se hacen visibles los botones para apliar y desampliar el mapa que vamos a crear

        mMap.uiSettings.isZoomControlsEnabled = true

        //Creo una variable con una latitud y longitud

        val centro = LatLng(42.23656846001073, -8.714151073325072)

        //Añado al mapa una marca a partir de la variable anterior

        mMap.addMarker(MarkerOptions().position(centro).title("Centro de explotacion legal"))

        //Hago que el mapa se centre en el punto deseado

        mMap.moveCamera(CameraUpdateFactory.newLatLng(centro))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun leerPermisos(): Boolean {

        when{

            //Si tengo permisos que me los diga

            ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED->{
                Log.i("Permisos","permiso garantozado")
                mensaje("Tienes Permisos")
                return true
            }

            //Si no los tengo que me aparezca un mensaje donde me diga que ponga los permisos requeridos en ajustes

            shouldShowRequestPermissionRationale (Manifest.permission.ACCESS_FINE_LOCATION
            )->{
                mensaje("Da permisos en ajustes")
                return false
            }

            //La primera vez que me pide los permisos  puedo aceptar o no

            else->{
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),PERMISO_LOCALIZACION)
                return false
            }
        }
    }

    //Aqui compruebo si le di correctamente los permisos

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISO_LOCALIZACION->{ //Conpruebo si mi permiso no esta vacio
                if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    mMap.isMyLocationEnabled = true //que se muestre en el mapa
                }
            }

            //Demas permisos

            else->{
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    //Esta funcion le muestar un mensajes al usuario

    fun mensaje(mensaje:String){
        Toast.makeText(this,mensaje, Toast.LENGTH_LONG).show()
    }

    //aqui se comprueba si se inicializa el mapa y la comprobacion de permisos

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private fun enableMyLocation(){
        if(!::mMap.isInitialized) return
        if(leerPermisos()){
            mMap.isMyLocationEnabled = true
        } else{
            leerPermisos()
        }
    }

}