package com.toonyoo.xiawenhao.cardreader;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android_serialport_api.BaseSerialPortDevice;

/**
 * Created by xiawenhao on 16/7/11.
 */
public class CardReader extends BaseSerialPortDevice {
    public static final String TAG = CardReader.class.getSimpleName();

    public final static byte[] START_CODE = {(byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x96, 0x69};

    public final int searchCardTime = 300;

    /**
     * command bytes to search card
     */
    private final static byte[] SEARCH_CARD_CMD = IDCardSenderCommandBuilder.GetSerchCardCommand();

    /**
     * command bytes to check sam state
     */
    private final static byte[] CHECK_SAM_V_CMD = IDCardSenderCommandBuilder.GetSAM_VCheckCmd();


    /**
     * command bytes to select card
     */
    private final static byte[] SELECT_CARD_CMD = IDCardSenderCommandBuilder.GetSelectCardCommand();


    /**
     * command bytes to read card info
     */
    private final static byte[] READ_STATIC_INFORMATION_CMD = IDCardSenderCommandBuilder.GetReadStaticInfomationCommand();

    /**
     * S50 card type
     */
    private final static byte[] S50_CARD_BYTES = {0x55, (byte)0xAA, 0x04, 0x56, (byte)0xFF, 0x08, 0x5a};

    /**
     * S70 card type
     */
    private final static byte[] S70_CARD_BYTES = {0x55, (byte)0xAA, 0x04, 0x56, (byte)0xFF, 0x18, 0x4a};

    /**
     * this is a callback object for autoSearching
     */
    private volatile AutoSearchingListener autoSearchingListener;


    /**
     * singleThreadPool to run searching can be reuse
     */
    private ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();


    /**
     * To init hardware
     * @param portPath
     * @param baudrate
     * @throws IOException
     */
    public CardReader(String portPath, int baudrate) throws IOException {
        this(new File(portPath), baudrate);
    }

    /**
     * To init hardware
     * @param portPathFile
     * @param baudrate
     * @throws IOException
     */
    public CardReader(File portPathFile, int baudrate) throws IOException {
        super(portPathFile, baudrate);
    }



    /**
     * this function will return the card number of card which you selected in byte[]
     * @return
     */
    public byte[] selectCard() throws IOException{
        byte[] bytes = execute(SELECT_CARD_CMD);

        if(bytes != null){
            if(checkResponseFormate(bytes)){
                SAM_V_STATE state = getSAMState(bytes);
                if(state != null){
                    if(state.equals(SAM_V_STATE.OpreationSuccess)){
                        return getDataBytes(bytes);

                    }else if(state.equals(SAM_V_STATE.SelectCardFailed)){
                        return null;
                    }
                }
            }
        }

        return null;
    }

    public IDCardInformation readInfo() throws IOException{
        byte[] bytes = execute(READ_STATIC_INFORMATION_CMD);

        if(bytes != null){
            SAM_V_STATE state = getSAMState(bytes);
            if(state == null || !state.equals(SAM_V_STATE.OpreationSuccess)){
                return null;
            }
        }

        byte[] data = getDataBytes(bytes);

        if(data != null){
            return new IDCardInformation(data);
        }else{
            return null;
        }

    }


    /**
     * this is the listener object for IDCard reader
     */
    public interface  AutoSearchingListener{
        /**
         * call this when card find
         */
        void onCardFind();

        /**
         * call this when read some information in this card
         */
        void onReadCardSuccess(IDCardInformation cardInfo);

        /**
         * call this when read card information failed
         */
        void onReadCardFailed();


        /**
         * call this if the card state is wrong
         * @param state error state
         */
        void onError(SAM_V_STATE state);
    }

    private volatile boolean isAutoSearching = false;


    /**
     * return if auto searching task is running
     * @return return true if ths task is running
     */
    public boolean isAutoSearching() {
        return isAutoSearching;
    }

    /**
     * 关闭自动寻卡 close auto searching card
     */
    public void stopAutoSearching(){
        isAutoSearching = false;
    }


    /**
     * 设置寻卡回调
     * @param listener 寻卡回调
     */
    public void setAutoSearchingListener(AutoSearchingListener listener){
        autoSearchingListener = listener;
    }

    /**
     * 获得寻卡回调
     * @return 寻卡回调
     */
    public AutoSearchingListener getAutoSearchingListener(){
        return autoSearchingListener;
    }

    /**
     * 开启自动寻卡 open autoSearching card  this is a async task ,do something you need in the listener
     */
    public void startAutoSearching(){
        if(isAutoSearching){
            Log.d(TAG, "Searching card is running , skip this startAutoSearching operation.");
            return;
        }

        singleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                isAutoSearching = true;

                do{
                    try {
                        byte[] bytes = searchCard();

                        if (bytes == null) {
                            continue;
                        }

                        SAM_V_STATE state = getSAMState(bytes);
                        if (state != null) {
                            if (state.equals(SAM_V_STATE.OpreationSuccess)) {
                                // don't care
                            } else if (state.equals(SAM_V_STATE.FindCardSuccess)) {

                                if(getAutoSearchingListener() != null){
                                    getAutoSearchingListener().onCardFind();
                                }else{
                                    continue;
                                }
                                CardReader.this.selectCard();
                                IDCardInformation info = readInfo();
                                if (info != null) {
                                    if(getAutoSearchingListener() != null){
                                        getAutoSearchingListener().onReadCardSuccess(info);
                                    }
                                } else {
                                    if(getAutoSearchingListener() != null){
                                        getAutoSearchingListener().onReadCardFailed();
                                    }
                                }
                            } else if (state.equals(SAM_V_STATE.FindCardFailed)) {
                                //don't care
                            } else {
                                if(getAutoSearchingListener() != null){
                                    getAutoSearchingListener().onError(state);
                                }
                            }
                        }
                    } catch (IOException e) {
                        if(getAutoSearchingListener() != null){
                            getAutoSearchingListener().onReadCardFailed();
                        }
                    }
                }while (isAutoSearching);
            }
        });

    }

    private byte[] getDataBytes(byte[] bytes){
        if (bytes == null){
            return null;
        }
        if(bytes.length > 11){
            int resultLength = bytes.length - 11;
            byte[] res = new byte[resultLength];

            System.arraycopy(bytes, 10, res, 0, resultLength);

            return res;
        }else{
            return null;
        }
    }


    public byte[] searchCard() throws IOException {
        return execute(SEARCH_CARD_CMD, searchCardTime);
    }



    public interface CheckSamVListener{
        void onSuccess();
        void onError(SAM_V_STATE state);
    }

    /**
     *检查返回的数据是否符合规范
     **/
    private boolean checkResponseFormate(byte[] response){
        byte[] startCode = Arrays.copyOfRange(response, 0, START_CODE.length);

        if(Arrays.equals(startCode, START_CODE)){
            byte len1 = response[START_CODE.length];
            byte len2 = response[START_CODE.length + 1];

            int length = ((len1 <<8)|len2)&0xffff;

            if(length == (response.length - 7)){
                byte bcc = response[response.length-1];
                byte caculated = 0x00;
                for(int index = START_CODE.length; index < (response.length - 1); index++){
                    caculated ^= response[index];
                }
                if(caculated == bcc){
                    return true;
                }
            }
        }

        Log.i(TAG, "Bytes check failed ,bytes is ;" + Arrays.toString(response));

        return false;
    }

    public SAM_V_STATE getSAMState(byte[] bytes){
        if (bytes.length < 10) {
            return null;
        }
        byte cmd = bytes[9];
        if(SAM_V_STATE.OpreationSuccess.cmd == cmd){
            return SAM_V_STATE.OpreationSuccess;
        }else if(SAM_V_STATE.FindCardSuccess.cmd == cmd){
            return SAM_V_STATE.FindCardSuccess;
        }else if(SAM_V_STATE.FindCardFailed.cmd == cmd){
            return SAM_V_STATE.FindCardFailed;
        }else if(SAM_V_STATE.SelectCardFailed.cmd == cmd){
            return SAM_V_STATE.SelectCardFailed;
        }else if(SAM_V_STATE.DoNotHaveThatFiled.cmd == cmd){
            return SAM_V_STATE.DoNotHaveThatFiled;
        }else if(SAM_V_STATE.BCCError.cmd == cmd){
            return SAM_V_STATE.BCCError;
        }else if(SAM_V_STATE.LenthError.cmd == cmd){
            return SAM_V_STATE.LenthError;
        }else if(SAM_V_STATE.CommandError.cmd == cmd){
            return SAM_V_STATE.CommandError;
        }else{
            return null;
        }
    }

    public enum SAM_V_STATE{
        OpreationSuccess    (0 ,(byte) 0x90 ,"操作成功"),
        FindCardSuccess     (1 ,(byte) 0x9F,"寻卡成功"),
        FindCardFailed      (2 ,(byte) 0x80 ,"寻卡失败"),
        SelectCardFailed    (3 ,(byte) 0x81 ,"选卡失败"),
        DoNotHaveThatFiled  (4 ,(byte) 0x91 ,"没有此项内容"),
        BCCError            (5 ,(byte) 0x10 ,"输入校验和错误"),
        LenthError          (6 ,(byte) 0x11 ,"输入长度错误"),
        CommandError        (7 ,(byte) 0x21 ,"输入命令错误");


        SAM_V_STATE(int code ,byte cmd ,String message){
            this.code       = code;
            this.cmd        = cmd;
            this.message    = message;
        }

        public int code;
        public byte cmd;
        public String message;

        @Override
        public String toString() {
            return "{code="+code+",byte="+cmd+",message="+message+"}";
        }
    }



}


