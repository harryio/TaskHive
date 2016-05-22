package com.harryio.taskhive.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Helper class to work with Assets.
 */
public class Assets {
    public static List<String> readSqlStatements(Context ctx, String name) {
        try {
            InputStream in = ctx.getAssets().open(name);
            Scanner scanner = new Scanner(in, "UTF-8").useDelimiter(";");
            List<String> result = new LinkedList<>();
            while (scanner.hasNext()) {
                String statement = scanner.next().trim();
                if (!"".equals(statement)) {
                    result.add(statement);
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
