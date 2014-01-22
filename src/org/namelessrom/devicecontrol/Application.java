/*
 *  Copyright (C) 2013 Alexander "Evisceration" Martinz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.namelessrom.devicecontrol;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import com.stericson.roottools.RootTools;

import org.namelessrom.devicecontrol.utils.PreferenceHelper;

import static org.namelessrom.devicecontrol.utils.constants.DeviceConstants.DC_FIRST_START;
import static org.namelessrom.devicecontrol.utils.constants.DeviceConstants.JF_EXTENSIVE_LOGGING;

public class Application extends android.app.Application {

    // Switch to your needs
    public static boolean IS_LOG_DEBUG = false;

    public static boolean HAS_ROOT = false;

    public static AlarmManager alarmManager;

    @Override
    public void onCreate() {
        super.onCreate();

        PreferenceHelper.getInstance(this);
        IS_LOG_DEBUG = PreferenceHelper.getBoolean(JF_EXTENSIVE_LOGGING, false);

        if (Application.IS_LOG_DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());

            RootTools.debugMode = true;
        }

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // we need to detect SU for some features :)
        try {
            HAS_ROOT = RootTools.isRootAvailable() && RootTools.isAccessGiven();
            if (HAS_ROOT) {
                RootTools.remount(Environment.getExternalStorageDirectory()
                        .getAbsolutePath(), "rw");
            }
        } catch (Exception exc) {
            logDebug("DetectSu: " + exc.getMessage());
        }

        // Set flag to enable BootUp receiver
        PreferenceHelper.setBoolean(DC_FIRST_START, false);

    }

    /**
     * Logs a message to logcat if boolean param is true.<br />
     * This is very useful for debugging, just set debug to false on a release build<br />
     * and it wont show any debug messages.
     *
     * @param msg The message to log
     */
    public static void logDebug(final String msg) {
        if (IS_LOG_DEBUG) {
            Log.e("DeviceControl", msg);
        }
    }
}
