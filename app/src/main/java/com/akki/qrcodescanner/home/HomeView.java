package com.akki.qrcodescanner.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akki.qrcodescanner.R;
import com.akki.qrcodescanner.camera.CameraSource;
import com.akki.qrcodescanner.camera.CameraSourcePreview;
import com.akki.qrcodescanner.camera.GraphicOverlay;
import com.akki.qrcodescanner.scan.BarcodeGraphic;
import com.akki.qrcodescanner.scan.BarcodeTrackerFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import static android.hardware.Camera.Parameters.FLASH_MODE_TORCH;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;

/**
 * Created by E01106 on 11/11/2017.
 */

public class HomeView extends LinearLayout implements HomeContract.View{

    private static final String TAG = HomeView.class.getSimpleName();

    private HomeContract.Presenter mPresenter;
    private ImageView mBtnNext;
    private ProgressBar mProgressBar;
    private LinearLayout mScannerLayout, mScanSuccessLayout, mScanFailLayout;

    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String BarcodeObject = "Barcode";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    // helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    private TextView mTicketPrice, mNationality, mTicketValidity, mTicketType, mScanTicketCountPass, mFailReason, mScanTicketCountFail, mScanStartHintText;

    //Temp vars: will remove later
    private TextView mQrCodeFormat, mQrValueFormat, mQrDisplayValue;

    // TODO: read these parameters from the settings: later
    boolean mAutoFocus = true;
    //boolean mUseFlash = false;

    public HomeView(Context context) {
        super(context);
        init();
    }

    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        Log.e(TAG, "init()_called");
        // Inflate layout here
        inflate(getContext(), R.layout.homeview_layout,this);

        resolveIds();

    }


    @Override
    public void setPresenter(@NonNull HomeContract.Presenter presenter) {

    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onHostException(String url) {

    }

    @Override
    public void showErrorSnackBar(String errorMsg) {

    }

    @Override
    public void showProgress(boolean flag) {

    }

    @Override
    public void hideProgress(boolean flag) {

    }

    @Override
    public void startScanner() {
        Log.d(TAG, "startScanner()");

        if(!mScannerLayout.isShown()) {
            mScannerLayout.setVisibility(VISIBLE);
        }

        if(mScanStartHintText.isShown()) {
            mScanStartHintText.setVisibility(GONE);
        }

        if(mScanSuccessLayout.isShown()) {
            mScanSuccessLayout.setVisibility(GONE);
        }
        if(mScanFailLayout.isShown()) {
            mScanFailLayout.setVisibility(GONE);
        }

        //Set Scanner start-stop icon
        ((HomeActivity)getContext()).setScannerIcon(true);

        startCameraSource();
    }

    @Override
    public void reStartScanner() {
        Log.d(TAG, "reStartScanner()");

        resolveIds();

        if(!mScannerLayout.isShown()) {
            mScannerLayout.setVisibility(VISIBLE);
        }

        if(mScanStartHintText.isShown()) {
            mScanStartHintText.setVisibility(GONE);
        }

        if(mScanSuccessLayout.isShown()) {
            mScanSuccessLayout.setVisibility(GONE);
        }
        if(mScanFailLayout.isShown()) {
            mScanFailLayout.setVisibility(GONE);
        }

        ///Set Scanner start-stop icon
        ((HomeActivity)getContext()).setScannerIcon(true);

        startCameraSource();

        //Reset Scan result showing flag
        ((HomeActivity)getContext()).setIfResultShowing(false);
    }

    @Override
    public void stopScanner() {
        Log.d(TAG, "stopScanner()");

        if (mPreview != null) {
            mPreview.release();
        }

        //Re-Set Scanner start-stop icon
        ((HomeActivity)getContext()).setScannerIcon(false);

        //Clear All view
        if(mScannerLayout.isShown()) {
            mScannerLayout.setVisibility(GONE);
        }
        if(mScanSuccessLayout.isShown()) {
            mScanSuccessLayout.setVisibility(GONE);
        }
        if(mScanFailLayout.isShown()) {
            mScanFailLayout.setVisibility(GONE);
        }

        if(!mScanStartHintText.isShown()) {
            mScanStartHintText.setVisibility(VISIBLE);
        }
    }

    @Override
    public void pauseScanner() {
        Log.d(TAG, "pauseScanner()_mPreview: " +mPreview);
        if (mPreview != null) {
            mPreview.stop();
        }
        Log.d(TAG, "After stop preview");
    }


    /* Hide scanner view and show result: scan success or fail */
    @Override
    public void showScanResult(boolean scanResult) {
        Log.d(TAG, "showScanResult()_scanResult: " +scanResult + "\n"
                +mScanSuccessLayout.isShown() + ":" +mScanFailLayout.isShown());

        pauseScanner();

        if(mScannerLayout.isShown()) {
            mScannerLayout.setVisibility(GONE);
        }
        if(mScanStartHintText.isShown()) {
            mScanStartHintText.setVisibility(GONE);
        }


        if(scanResult) {
            //show result success view
            //mScanSuccessLayout.setVisibility(VISIBLE);
            showQrCodeScanResult(); //Temp Code
        } else {
            //show result failure view
            mScanFailLayout.setVisibility(VISIBLE);
        }

        //Set Scan result showing flag
        ((HomeActivity)getContext()).setIfResultShowing(true);

    }

    /* Temp function */
    private void showQrCodeScanResult() {
        Log.d(TAG, "showQrCodeScanResult()");

        if(((HomeActivity)getContext()).getBarCodeObject() != null) {
            mQrCodeFormat.setText(String.valueOf(((HomeActivity)getContext()).getBarCodeObject().format));
            mQrValueFormat.setText(String.valueOf(((HomeActivity)getContext()).getBarCodeObject().valueFormat));
            mQrDisplayValue.setText(((HomeActivity)getContext()).getBarCodeObject().displayValue);
        }
        mScanSuccessLayout.setVisibility(VISIBLE);
    }

    public void resolveIds() {
        mScannerLayout = (LinearLayout) findViewById(R.id.layout_scanner);
        mScanSuccessLayout = (LinearLayout) findViewById(R.id.layout_scan_result_success);
        mScanFailLayout = (LinearLayout) findViewById(R.id.layout_scan_result_fail);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.graphicOverlay);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(mAutoFocus, ((HomeActivity)getContext()).getFlashStatus());
        } else {
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(getContext(), new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());

        Snackbar.make(mGraphicOverlay, getResources().getString(R.string.camera_pinch_zoom_hint),
                Snackbar.LENGTH_LONG)
                .show();

        //Success Result IDs:
        /*mTicketPrice = (TextView) findViewById(R.id.txt_ticket_price);
        mNationality = (TextView) findViewById(R.id.txt_nationality);
        mTicketValidity = (TextView) findViewById(R.id.txt_validity);
        mTicketType = (TextView) findViewById(R.id.txt_ticket_type);
        mScanTicketCountPass = (TextView) findViewById(R.id.txt_ticket_count_pass);
        Button scanNext = (Button) findViewById(R.id.btn_scan_next);

        scanNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reStartScanner();
            }
        });*/

        //Temp Code_start: It will be replaced by above commented Success Result IDs
        mQrCodeFormat = (TextView) findViewById(R.id.txt_format);
        mQrValueFormat = (TextView) findViewById(R.id.txt_value_format);
        mQrDisplayValue = (TextView) findViewById(R.id.txt_display_value);
        Button scanNext = (Button) findViewById(R.id.btn_scan_next);
        scanNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reStartScanner();
            }
        });
        //Temp Code_end

        //Failure Result IDs:
        mFailReason = (TextView) findViewById(R.id.txt_fail_reason);
        mScanTicketCountFail = (TextView) findViewById(R.id.txt_ticket_count_fail);
        Button resumeScan = (Button) findViewById(R.id.btn_resume_scanning);

        resumeScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reStartScanner();
            }
        });

        mScanStartHintText = (TextView) findViewById(R.id.txt_hint_start_scan);
    }


    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale((HomeActivity)getContext(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions((HomeActivity)getContext(), permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = (HomeActivity)getContext();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        findViewById(R.id.topLayout).setOnClickListener(listener);
        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || super.onTouchEvent(e);
    }*/

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Log.d(TAG, "AutoFocus: " +autoFocus + "::FlashStatus: " +useFlash);
        Context context = getContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, getContext());
        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = getContext().registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(getContext(), R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getResources().getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        CameraSource.Builder builder = new CameraSource.Builder(getContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
               // .setRequestedPreviewSize(1600, 1600)
                .setRequestedFps(15.0f);

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? FLASH_MODE_TORCH : null)
                .build();
    }


    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog((HomeActivity)getContext(), code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


    /**
     * onTap returns the tapped barcode result to the calling Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {
        // Find tap point in preview frame coordinates.
        int[] location = new int[2];
        mGraphicOverlay.getLocationOnScreen(location);
        float x = (rawX - location[0]) / mGraphicOverlay.getWidthScaleFactor();
        float y = (rawY - location[1]) / mGraphicOverlay.getHeightScaleFactor();

        // Find the barcode whose center is closest to the tapped point.
        Barcode best = null;
        float bestDistance = Float.MAX_VALUE;
        for (BarcodeGraphic graphic : mGraphicOverlay.getGraphics()) {
            Barcode barcode = graphic.getBarcode();
            if (barcode.getBoundingBox().contains((int) x, (int) y)) {
                // Exact hit, no need to keep looking.
                best = barcode;
                break;
            }
            float dx = x - barcode.getBoundingBox().centerX();
            float dy = y - barcode.getBoundingBox().centerY();
            float distance = (dx * dx) + (dy * dy);  // actually squared distance
            if (distance < bestDistance) {
                best = barcode;
                bestDistance = distance;
            }
        }

        return best != null;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }

}
