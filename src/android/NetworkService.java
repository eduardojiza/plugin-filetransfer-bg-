package com.red_folder.phonegap.plugin.backgroundservice.sample;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Eduardo Jimenez on 13/01/2016.
 */
public class NetworkService extends BackgroundService {
    private static final String TAG = "FILE TRANSFER";
    private static final String CHARSET = "UTF-8";
    private static final String KEY_ARRAY = "files";
    private static final String KEY_FILE_PATH = "filePath";
    private static final String KEY_FILE_NAME = "fileName";
    private static final String KEY_SERVER = "server";
    private static final String KEY_PARAMS = "params";
    private static final String USER_AGENT = "inffinix";
    private JSONObject configuration;
    private HttpFileUploader httpFileUploader;
    private JSONObject params;
    private JSONObject result;
    private JSONArray elements;
    private JSONArray elementsResponse;
    private JSONObject element;
    private String filePath;
    private String server;
    private String fileName;
    private List<String> response;


    @Override
    protected JSONObject doWork() {
        //configuration was initializing on setConfig
        result = null;
        if ( configuration != null ){
            try {
                result = new JSONObject();
                elementsResponse = new JSONArray();

                elements = configuration.getJSONArray( KEY_ARRAY );
                for( int i = 0; i < elements.length(); i++ ) {
                    element = elements.getJSONObject( i );

                    //it checks required values
                    if( element.has( KEY_FILE_PATH ) && element.has( KEY_SERVER ) && element.has( KEY_FILE_NAME ) ) {
                        filePath = element.getString( KEY_FILE_PATH );
                        server = element.getString( KEY_SERVER );
                        fileName = element.getString( KEY_FILE_NAME );
                        Log.v(TAG, KEY_FILE_PATH + " = " + filePath + " ,  " + KEY_SERVER + " = " + server + " ,  " + KEY_FILE_NAME + " = " + fileName);
                        httpFileUploader = new HttpFileUploader( server, CHARSET );
                        httpFileUploader.addHeaderField("User-Agent", USER_AGENT);

                        File sourceFile = new File( filePath );
                        if( sourceFile.exists() ){
                            httpFileUploader.addFilePart( fileName, sourceFile );
                        }

                        if ( element.has( KEY_PARAMS ) ) {
                            params = element.getJSONObject( KEY_PARAMS );
                            for ( int j = 0; j < params.names().length(); j++ ) {
                                Log.v( TAG, "key = " + params.names().getString( j ) + " value = " + params.get( params.names().getString( j ) ) );
                                httpFileUploader.addFormField(params.names().getString(j), params.get(params.names().getString(j)).toString());
                            }
                        }
                    }

                    //it processes response
                    response = httpFileUploader.finish();
                    System.out.println("SERVER REPLIED:");
                    for ( String line : response ) {
                        System.out.println( line );
                    }

                    elementsResponse.put( element );
                }

                //return to js
                result.put( KEY_ARRAY, elementsResponse );

            } catch ( JSONException e ) {
                e.printStackTrace();
                configuration = null;
            } catch ( IOException e ) {
                e.printStackTrace();
                configuration = null;
            }
        }

        configuration = null;
        return result;
    }

    @Override
    protected JSONObject getConfig() {
        JSONObject result = new JSONObject();
        Log.d( TAG, "--------------------- getConfig -----------------------" );
        return result;
    }

    @Override
    protected void setConfig( JSONObject config ) {
        Log.d(TAG, "--------------------- setConfig ---------------------");
        if ( config.has( KEY_ARRAY ) ){
            configuration = config;
        } else {
            configuration = null;
        }
    }

    @Override
    protected JSONObject initialiseLatestResult() {
        JSONObject result = new JSONObject();
        return result;
    }
}