package com.chedifier.cleaner.base;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2017/8/19.
 */

public class CommandUtils {

    private static final String TAG = "CommandUtils";

    /**
     * 描述：执行命令.
     *
     * @param command
     * @param workdirectory
     * @return
     */
    public static String runCommand(String[] command, String workdirectory) {
        StringBuilder result = new StringBuilder(128);
        Log.i(TAG, "run command: " + command);
        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            // set working directory
            if (workdirectory != null) {
                builder.directory(new File(workdirectory));
            }
            builder.redirectErrorStream(true);
            Process process = builder.start();

            InputStream in = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                result.append(line + "\n");
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}
