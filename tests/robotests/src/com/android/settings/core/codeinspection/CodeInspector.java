/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.core.codeinspection;

import com.google.common.truth.Truth;

import org.junit.Assert;
import org.robolectric.shadows.ShadowApplication;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Inspector takes a list of class objects and perform static code analysis in its {@link #run()}
 * method.
 */
public abstract class CodeInspector {

    protected static final String PACKAGE_NAME = "com.android.settings";

    protected static final String TEST_CLASS_SUFFIX = "Test";
    private static final String TEST_INNER_CLASS_SIGNATURE = "Test$";

    protected final List<Class<?>> mClasses;

    public CodeInspector(List<Class<?>> classes) {
        mClasses = classes;
    }

    /**
     * Code inspection runner method.
     */
    public abstract void run();

    protected boolean isConcreteSettingsClass(Class clazz) {
        // Abstract classes
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }
        final String packageName = clazz.getPackage().getName();
        // Classes that are not in Settings
        if (!packageName.contains(PACKAGE_NAME + ".")) {
            return false;
        }
        final String className = clazz.getName();
        // Classes from tests
        if (className.endsWith(TEST_CLASS_SUFFIX)) {
            return false;
        }
        if (className.contains(TEST_INNER_CLASS_SIGNATURE)) {
            return false;
        }
        return true;
    }

    public static void initializeGrandfatherList(List<String> grandfather, String filename) {
        try {
            final InputStream in = ShadowApplication.getInstance().getApplicationContext()
                    .getAssets()
                    .open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                grandfather.add(line);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error initializing grandfather " + filename, e);
        }

    }
}
