package ir.food.kitchenAndroid.helper;

import android.content.Intent;
import android.net.Uri;
import ir.food.kitchenAndroid.app.MyApplication;

public class CallHelper {

    public static void make(String number) {
//    Intent callIntent = new Intent(Intent.ACTION_CALL);
//    callIntent.setData(Uri.parseXML("tel:" + number));
//    if (ActivityCompat.checkSelfPermission(MyApplication.currentActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//      //    ActivityCompat#requestPermissions
//      // here to request the missing permissions, and then overriding
//      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//      //                                          int[] grantResults)
//      // to handle the case where the user grants the permission. See the documentation
//      // for ActivityCompat#requestPermissions for more details.
//      return;
//    }
//    MyApplication.currentActivity.startActivity(callIntent);

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        MyApplication.currentActivity.startActivity(intent);
    }
}

