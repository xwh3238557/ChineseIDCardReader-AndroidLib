//package com.toonyoo.xiawenhao.cardreader;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//
//import org.w3c.dom.Text;
//
//import java.io.IOException;
//import java.util.Arrays;
//
//public class MainActivity extends AppCompatActivity {
//
//    private byte[] cardNumber;
//    private TextView cardNumberView;
//
//    private TextView nameView;
//    private TextView sexView;
//    private TextView nationView;
//    private TextView birthdayView;
//    private TextView addressView;
//    private TextView idNumberView;
//    private TextView issuingAuthorityView;
//    private TextView issuingDateView;
//    private TextView endDateView;
//
//    private ImageView iconView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        final TextView tv = (TextView) findViewById(R.id.result);
//
//        cardNumberView = (TextView) findViewById(R.id.card_number);
//
//        nameView = (TextView) findViewById(R.id.textview_name);
//        sexView = (TextView) findViewById(R.id.textview_sex);
//        nationView = (TextView) findViewById(R.id.textview_nation);
//        birthdayView = (TextView) findViewById(R.id.textview_birthday);
//        addressView = (TextView) findViewById(R.id.textview_address);
//        idNumberView = (TextView) findViewById(R.id.textview_id_number);
//        issuingAuthorityView = (TextView) findViewById(R.id.textview_issuing_authority);
//        issuingDateView = (TextView) findViewById(R.id.textview_issuing_date);
//        endDateView = (TextView) findViewById(R.id.textview_end_date);
//
//        iconView = (ImageView) findViewById(R.id.imageview_icon);
//
//        String serialPortPath = "/dev/ttySAC2";
//        int baudrate = 115200;
//
//        try {
//            final CardReader cr = new CardReader(serialPortPath, baudrate);
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    cr.startAutoSearching(new CardReader.AutoSearchingListener() {
//
//                        @Override
//                        public void onCardFind() {
//                            MainActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    tv.setText("寻卡成功");
//                                }
//                            });
//                            byte[] cardNumber = cr.selectCard();
//
//                            MainActivity.this.runOnUiThread(new CardNumberRunnable(cardNumber));
//
//                            IDCardInformation info = cr.readInfo();
//
//                            runOnUiThread(new CardInfoRunnable(info));
//
//                        }
//
//                        @Override
//                        public void onCardNotFind() {
//                            MainActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    tv.setText("寻卡失败");
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onError(CardReader.SAM_V_STATE state) {
//                            MainActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    tv.setText("寻卡失败");
//                                }
//                            });
//                        }
//
//
//                    });
//                }
//            }).start();
//
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private class CardNumberRunnable implements Runnable{
//        private byte[] cardNumber;
//
//        public CardNumberRunnable(byte[] cardNumber){
//            this.cardNumber = cardNumber;
//        }
//
//
//        @Override
//        public void run() {
//            if(cardNumber != null){
//                MainActivity.this.cardNumberView.setText(Arrays.toString(cardNumber));
//            };
//        }
//    }
//
//    private class CardInfoRunnable implements Runnable{
//        private IDCardInformation info;
//
//        public CardInfoRunnable(IDCardInformation info){
//            this.info = info;
//        }
//
//
//        @Override
//        public void run() {
//            if(info != null) {
//                nameView.setText(info.getName());
//                nationView.setText(info.getNation());
//                sexView.setText(info.getSex());
//                birthdayView.setText(info.getBirthday());
//                addressView.setText(info.getAddress());
//                idNumberView.setText(info.getIDNumber());
//                issuingAuthorityView.setText(info.getIssuingAuthority());
//                issuingDateView.setText(info.getIssuingDate());
//                endDateView.setText(info.getEndDate());
//
//                iconView.setImageBitmap(info.getPicture());
//            }
//        }
//    }
//}
