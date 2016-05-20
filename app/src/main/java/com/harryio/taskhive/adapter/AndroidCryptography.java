package com.harryio.taskhive.adapter;

import com.harryio.taskhive.util.PRNGFixes;

import ch.dissem.bitmessage.cryptography.sc.SpongyCryptography;

public class AndroidCryptography extends SpongyCryptography {
    public AndroidCryptography() {
        PRNGFixes.apply();
    }
}
