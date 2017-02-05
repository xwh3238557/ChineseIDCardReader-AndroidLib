package com.toonyoo.xiawenhao.cardreader;

/**
 * Created by xiawenhao on 16/7/28.
 */
public class IDCardSenderCommandBuilder extends  CommandBuilder{
    public static final byte SEARCH_CARD_CMD = 0x20;
    public static final byte SEARCH_CARD_PARAM = 0x01;

    public static final byte SAM_V_CHECK_CMD = 0x11;
    public static final byte SAM_V_CHECK_PARAM = (byte)0xff;

    public static final byte SELECT_CARD_CMD = 0x20;
    public static final byte SELECT_CARD_PARAM = 0x02;

    public static final byte READ_STATIC_INFORMATION_CMD = 0x30;
    public static final byte READ_STATIC_INFOMATION_PARAM = 0x01;

    public byte[] startCode = {(byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x96, 0x69};

    public byte len1 = 0x00;

    public byte len2 = 0x03;

    public byte cmd;

    public byte param;


    public byte bcc;



    public IDCardSenderCommandBuilder(){

    }

    @Override
    public byte[] build() {
        byte[] bytes = new byte[10];

        System.arraycopy(startCode ,0 ,bytes, 0 ,startCode.length);
        bytes[5] = len1;
        bytes[6] = len2;
        bytes[7] = cmd;
        bytes[8] = param;

        return getCmdWithBCC(bytes);
    }

    private byte[] getCmdWithBCC(byte[] cmdWithoutBcc){
        bcc = 0;
        for(int index = 5; index < cmdWithoutBcc.length; index++){
            bcc ^= cmdWithoutBcc[index];
        }
        byte[] cmd = new byte[cmdWithoutBcc.length];
        System.arraycopy(cmdWithoutBcc ,0 ,cmd ,0 ,cmdWithoutBcc.length);
        cmd[cmd.length-1] = bcc;
        return cmd;
    }

    public static byte[] GetSAM_VCheckCmd(){
        IDCardSenderCommandBuilder builder = new IDCardSenderCommandBuilder();
        builder.cmd = SAM_V_CHECK_CMD;
        builder.param = SAM_V_CHECK_PARAM;
        return builder.build();
    }

    public static byte[] GetSerchCardCommand(){
        IDCardSenderCommandBuilder builder = new IDCardSenderCommandBuilder();
        builder.cmd = SEARCH_CARD_CMD;
        builder.param = SEARCH_CARD_PARAM;
        return builder.build();
    }

    public static byte[] GetSelectCardCommand(){
        IDCardSenderCommandBuilder builder = new IDCardSenderCommandBuilder();
        builder.cmd     = SELECT_CARD_CMD;
        builder.param   = SELECT_CARD_PARAM;
        return builder.build();
    }

    public static byte[] GetReadStaticInfomationCommand(){
        IDCardSenderCommandBuilder builder = new IDCardSenderCommandBuilder();
        builder.cmd    = READ_STATIC_INFORMATION_CMD;
        builder.param  = READ_STATIC_INFOMATION_PARAM;
        return builder.build();
    }
}
