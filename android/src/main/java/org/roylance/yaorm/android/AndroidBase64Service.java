package org.roylance.yaorm.android;

import android.util.Base64;

import org.jetbrains.annotations.NotNull;
import org.roylance.common.service.IBase64Service;

public class AndroidBase64Service
        implements IBase64Service {
    @NotNull
    @Override
    public String serialize(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    @NotNull
    @Override
    public byte[] deserialize(String s) {
        return Base64.decode(s, Base64.DEFAULT);
    }
}
