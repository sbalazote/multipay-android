package com.mercadopago.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

public class BaseTest <T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    public BaseTest(final Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected Context getApplicationContext() {

        return getInstrumentation().getContext();
    }

    protected void assertFinishCalledWithResult(Activity activity, int resultCode) {

        assertTrue(activity.isFinishing());
        try {
            Field field = Activity.class.getDeclaredField("mResultCode");
            field.setAccessible(true);
            int actualResultCode = (Integer) field.get(activity);
            assertTrue(actualResultCode == resultCode);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Looks like the Android Activity class has changed it's private fields for mResultCode or mResultData.Time to update the reflection code.", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ActivityResult getActivityResult(Activity activity) {

        try {
            ActivityResult activityResult = new ActivityResult();
            // Result code
            Field field = Activity.class.getDeclaredField("mResultCode");
            field.setAccessible(true);
            activityResult.setResultCode((Integer) field.get(activity));
            // Extras
            field = Activity.class.getDeclaredField("mResultData");
            field.setAccessible(true);
            Intent resultData = (Intent) field.get(activity);
            if (resultData != null) {
                activityResult.setExtras(resultData.getExtras());
            }
            // Return
            return activityResult;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Looks like the Android Activity class has changed it's private fields for mResultCode or mResultData.Time to update the reflection code.", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static <T> void putListExtra(Intent intent, String listName, List<T> list) {

        if (list != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<T>>(){}.getType();
            intent.putExtra(listName, gson.toJson(list, listType));
        }
    }

    protected void sleepThread() {

        sleepThread(3000);
    }

    protected void sleepThread(int milliseconds) {

        try {
            Thread.sleep(milliseconds);
        } catch (Exception ex) {
            // do nothing
        }
    }
}
