package com.toonyoo.xiawenhao.cardreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.InputFilter;
import android.util.Xml;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by xiawenhao on 16/8/1.
 */
public class IDCardInformation implements Serializable {

    private int textLength;

    private int pictureLength;


    private String mName;
    private String mSex;
    private String mNation;
    private String mBirthday;
    private String mAddress;
    private String mIDNumber;
    private String mIssuingAuthority;
    private String mIssuingDate;
    private String mEndDate;

    private byte[] picByte;

    private Bitmap mPicture;

    public Bitmap getPicture(){
        return mPicture;
    }

    public byte[] getPictureByte(){
        return  picByte;
    }

    public String getName() {
        return mName;
    }

    public String getSex() {
        return mSex;
    }

    public String getNation() {
        return mNation;
    }

    public String getBirthday() {
        return mBirthday;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getIDNumber() {
        return mIDNumber;
    }

    public String getIssuingAuthority() {
        return mIssuingAuthority;
    }

    public String getIssuingDate() {
        return mIssuingDate;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public IDCardInformation(byte[] byteArr){
        byte[] raw = byteArr;

        dealWithRawByte(raw);
    }

    private void dealWithRawByte(byte[] bytes){
        textLength = (bytes[0]<<8 | bytes[1]) & 0xffff;
        pictureLength = (bytes[2]<<8 | bytes[3]) & 0xffff;

        byte[] textBytes = new byte[textLength];
        byte[] pictureBytes = new byte[pictureLength];
        picByte = pictureBytes;
        System.arraycopy(bytes ,4 ,textBytes ,0 ,textLength);
        System.arraycopy(bytes ,textLength+4 ,pictureBytes, 0, pictureLength);

        decodeText(textBytes);
        decodePicture(pictureBytes);
    }


    private byte[] getReverselBytes(byte[] src , int startIndex, int length){
        byte[] res = Arrays.copyOfRange(src, startIndex, startIndex+length);

        for(int index = 0; index < res.length; index += 2){
            byte byte_1 = res[index];
            byte byte_2 = res[index + 1];

            res[index] = byte_2;
            res[index+1] = byte_1;
        }

        return res;
    };

    private void decodeText(byte[] bytes){
        byte[] nameBytes = new byte[30];
        byte[] sexBytes = new byte[2];
        byte[] nationBytes = new byte[4];
        byte[] birthdayBytes = new byte[16];
        byte[] addressBytes = new byte[70];
        byte[] idNumberBytes = new byte[36];
        byte[] issuingAuthorityBytes = new byte[30];
        byte[] issuingDateBytes = new byte[16];
        byte[] endDateBytes = new byte[16];

        int index = 0;
        nameBytes = getReverselBytes(bytes, index, nameBytes.length);
        index += nameBytes.length;
        sexBytes = getReverselBytes(bytes, index, sexBytes.length);
        index += sexBytes.length;
        nationBytes = getReverselBytes(bytes, index, nationBytes.length);
        index += nationBytes.length;
        birthdayBytes = getReverselBytes(bytes, index, birthdayBytes.length);
        index += birthdayBytes.length;
        addressBytes = getReverselBytes(bytes, index, addressBytes.length);
        index += addressBytes.length;
        idNumberBytes = getReverselBytes(bytes, index, idNumberBytes.length);
        index += idNumberBytes.length;
        issuingAuthorityBytes = getReverselBytes(bytes, index, issuingAuthorityBytes.length);
        index += issuingAuthorityBytes.length;
        issuingDateBytes = getReverselBytes(bytes, index, issuingDateBytes.length);
        index += issuingDateBytes.length;
        endDateBytes = getReverselBytes(bytes, index, endDateBytes.length);


        mName = new String(nameBytes, Charset.forName("UTF-16")).trim();
        mSex = new String(sexBytes, Charset.forName("UTF-16")).trim();
        mNation = new String(nationBytes, Charset.forName("UTF-16")).trim();
        mBirthday = new String(birthdayBytes, Charset.forName("UTF-16")).trim();
        mAddress = new String(addressBytes, Charset.forName("UTF-16")).trim();
        mIDNumber = new String(idNumberBytes, Charset.forName("UTF-16")).trim();
        mIssuingAuthority = new String(issuingAuthorityBytes, Charset.forName("UTF-16")).trim();
        mIssuingDate = new String(issuingDateBytes, Charset.forName("UTF-16")).trim();
        mEndDate = new String(endDateBytes, Charset.forName("UTF-16")).trim();
    }

    private void decodePicture(byte[] bytes){

        mPicture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


}
