package com.mj.app.print.util;

public class PrintLabel {

    /*
    * 请将所有的打印标签保存在这里
    *
    * */
    public final static String SAMPLE_LABEL = "^XA^FO100,75^BY3^B3N,N,100,Y,N^FD123ABC^XZ";

    /**
     * @param value
     * @return 斑马打印机需要的二维码指令 格式是utf-8
     * BQ,2,10  2代表模式2
     */
    public final static String ZebraLabel(String value) {
        return "^XA" +
                "^FO2,2" +
                "^BQ,3,7" +
                "^FDHA," + value + "^FS" +
                "^XZ";
    }

    /**
     * @param value
     * @return 西铁城打印机需要的二维码指令 格式是utf-8
     * BQ,2,10  2代表模式2
     */
    public final static String CitizenLabel(String value) {
        return "^XA" +
                "^FO12,40" +
                "^BQN,3,10" +
                "^FDHA," + value + "^FS" +
                "^XZ";
    }

    //打印二维码
    public static String QRCode(String value) {
        return "^XA" +
                "^FO25,0" +
                "^BQ,2,5" +
                "^FDHA," + value + "^FS" +
                "^XZ";
    }
    //^XA^FO20,10,^AEN,56,30^FDZEBAR^FS^XZ


    public static String test1(){
        return "^XA\n" +
                "^SEE:GB18030.DAT^FS\n" +
                "^CW1,E:SIMSUN.FNT^FS\n" +
                "^FO50,30^A1N,24,24^CI26^FD中华人民共和国CHINA^FS\n" +
                "^FO50,70^A1N,24,24^CI26^FD上海京威电子科技有限公司WINFUTURE^FS\n" +
                "^FO50,130^A0N,36,36^CI0^FDWinFuture Technologies,Inc.^FS\n" +
                "^PR3\n" +
                "^PQ1\n" +
                "^XZ";

    }

    //ok
    public static String test2(String number,String name,String phone,String address,String type){
        return "CT~~CD,~CC^~CT~\n" +
                "^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR2,2~SD15^JUS^LRN^CI0^XZ\n" +
                "^XA\n" +
                "^CW1,E:SIMSUN.FNT^FS\n" +
                "^MMT\n" +
                "^PW609\n" +
                "^LL0406\n" +
                "^LS0\n" +
                "^FO0,0^GFA,26752,26752,00076,:Z64:\n" +
                "eJzt3cENgzAQRNH1iTJcCqWFUimDQ4SDaYDLHFD0fgFPWhcwrpKkp/qIdFxUy1BjbFVLytpjJ95Hrinrm7U+KevMWvP9Ey3jsraM1aaVoW4oaLWTxWKxWCwWi8VisVgsFovFYrFYLBaLxWKxWCwWi8VisVgsFovFYrFYLBaLxWKxWCwWi8VisVgsFovFYrFYLBaLxWKxWCwWi8VisVgsFovFYk2rXropnt1Nj/Xezfo1ZYU3/nvKOrL/KyT/fdBf9ANgIARQ:8EC7\n" +
                "^FO13,180^GB575,0,4^FS\n" +
                "^FO15,98^GB574,0,3^FS\n" +
                "^FO18,257^GB574,0,2^FS\n" +
                "^FT38,67^A1N,24,24^CI17^F8^FD订单号^FS\n" +
                "^FT37,150^A1N,24,24^CI17^F8^FD姓名^FS\n" +
                "^FT300,152^A1N,24,24^CI17^F8^FD联系方式^FS\n" +
                "^FT37,226^A1N,24,24^CI17^F8^FD地址^FS\n" +
                "^FT37,296^A1N,24,24^CI17^F8^FD冰箱型号^FS\n" +
                "^FT289,398^A0N,28,28^CI0\\^FD2015/12/28^FS\n" +
                "^FT430,397^A0N,28,28^CI0\\^FD06:47:59 PM^FS\n" +
                "^FT156,68^A0N,28,28^CI0\\^FD"+number+"^FS\n" +
                "^FT118,153^A1N,24,24^CI17^F8\\^FD"+name+"^FS\n" +
                "^FT416,152^A0N,28,28^CI0\\^FD"+phone+"^FS\n" +
                "^FT144,235^A1N,24,24^CI17^F8\\^FD"+address+"^FS\n" +
                "^FT129,306^A0N,28,28^CI0\\^FD"+type+"^FS\n" +
                "^PQ1,0,1,Y^XZ";
    }


    //正常
    //ANSI时需要码表GB18030.DAT，使用^CI26 ；
    //UTF-8时，没有码表，使用^CI17和^F8；
    public static String test4(){
        return "^XA\n" +
                "^CW1,E:SIMSUN.FNT^FS\n" +
                "^FO50,30^A1N,24,24^CI17^F8^FD中华人民共和国CHINA^FS\n" +
                "^FO50,70^A1N,24,24^CI17^F8^FD上海京威电子科技有限公司WINFUTURE^FS\n" +
                "^FO50,130^A0N,36,36^CI0^FDWinFuture Technologies,Inc.^FS\n" +
                "^BY3,3.0^FS\n" +
                "^FO50,180^BEN,160,Y,N^FD690123456789^FS\n" +
                "^PR3\n" +
                "^PQ1\n" +
                "^XZ";
    }

}
