package com.toonyoo.xiawenhao.cardreader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiawenhao on 16/7/26.
 */
public class CardSenderCommandBuilder extends CommandBuilder{
    private static final byte[] START_CODE = {0x55, (byte)0xaa};
    private static final byte SEARCH_CARD_COMMAND_BYTE = (byte)0x01;
    private static final byte[] SEARCH_CARD_DATA_BYTE = new byte[]{(byte)0xff};

    public final byte frameCode = (byte) 0xff;

    private byte length;

    public byte command;

    public CardSenderCommandBuilder setCommand(byte command) {
        this.command = command;
        return this;
    }

    public byte data[];

    public CardSenderCommandBuilder setData(byte[] data){
        this.data = data;
        return this;
    }

    public CardSenderCommandBuilder(){
        startCode = START_CODE;

    }

    @Override
    public byte[] build() {
        List<Byte> resultList = new ArrayList<>();

        appendArrayToList(startCode, resultList);

        length = (byte)(1 + 1 + data.length + 1);

        resultList.add(length);

        resultList.add(command);

        resultList.add(frameCode);

        appendArrayToList(data, resultList);

        addBCC(resultList);

        return getByteArray(resultList);
    }

    private void appendArrayToList(byte[] src, List<Byte> des){
        for(byte b : src){
            des.add(b);
        }
    }

    private void addBCC(List<Byte> list){
        byte res = 0;

        for(int index = 0; index < list.size(); index++){
            res ^= list.get(index);
        }

        list.add(res);

    }

    private byte[] getByteArray(List<Byte> byteList){
        int length = byteList.size();
        byte[] byteArray = new byte[length];
        for(int index = 0; index < length ;index++){
            byteArray[index] = byteList.get(index);
        }
        return byteArray;
    }

    public static byte[] GetSearchCardCommond(){
        return new CardSenderCommandBuilder()
                .setCommand(SEARCH_CARD_COMMAND_BYTE)
                .setData(SEARCH_CARD_DATA_BYTE)
                .build();
    }

}
