package com.toonyoo.xiawenhao.cardreader;

import android.content.SyncAdapterType;

/**
 * Created by xiawenhao on 16/7/29.
 */
public enum  CardReaderError{
    Unknown(0, "未知的错误");

    CardReaderError(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int code;
    public String msg;

    @Override
    public String toString() {
        return "{Code="+code+",Message="+msg+"}";
    }
}
