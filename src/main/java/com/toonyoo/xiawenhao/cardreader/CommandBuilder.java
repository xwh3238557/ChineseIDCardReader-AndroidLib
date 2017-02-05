package com.toonyoo.xiawenhao.cardreader;

import android.os.Build;

/**
 * Created by xiawenhao on 16/7/26.
 */
public abstract class CommandBuilder {
    public byte[] startCode;

    public byte[] endCode;

    public byte bcc;

    public abstract byte[] build();
}
