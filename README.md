gradle:2.1.0

taox M线套箱扫码
comapi 串口


1、PLC发送的请求消息为3个字节：
正常请求套箱型号时前两个字节为：‘xh’
通讯测试时前两个字节为：‘cs’
第三个字节，为校验码。
正常时校验码会在85（2进制01010101）和42（2进制10101010）之间反复切换。
若通讯受到干扰，PLC接收到的反馈消息校验不成功，或PLC发送后5秒内未接受到反馈消息，PLC会重发请求消息。重发请求消息中校验码不切换。
举个例子：PLC 第一次发送消息‘xh’+85 ，然后接受到APP返回的消息，并校验成功。
              第二次发送消息‘xh’+42 ，然后接受到APP返回的消息，并校验成功。
              第三次发送消息‘xh’+85 ，然后接受到APP返回的消息，并校验成功。
              第三次发送消息‘xh’+42 ，然后接受到APP返回的消息，但校验失败。（或者没有收到APP发送回来的消息。)
              第四次发送消息‘xh’+42 ，然后接受到APP返回的消息，并校验成功。
              第五次发送消息‘xh’+85 
2、APP返回的反馈消息也为3个字节：
第一个字节为：套箱型号，第二个字节为：料仓号
第三个字节，为PLC发送过来的校验码。
特殊情况：通讯测试和队列中无条码时，第三个字节正常返回校验码，第一个，第二个字节均赋值为51（2进制11001100）.
 
APP每次收到PLC发送来的消息，先把对校验字节进行判断。若校验字节的值不是85或42，则不予以处理。若校验字节的值为85或42，则判断是否与上次的校验字节的值相同，若与上次的校验字节相同，则重发上次发送的套箱号与程序号；若不同，则从队列中抽取一对套箱号与程序号发送给PLC。
具体而言：APP中建三个字节型变量：
套箱型号：byte1
料仓号：byte2
校验字节：byte3
APP启动时把byte1,byte2，byte3全部清零。
 
APP接收到PLC发来的信息时，解析到该消息的校验字节为byteRcv;
然后进行判断：
1.若byteRcv ！=85或42则，则忽略当前消息；提前返回。
2.若byteRcv  ==85或42，则比较byteRcv 和 byte3：
2.1若byteRcv != byte3 ，
     2.1.1消息字符不是'xh'或'cs'，则忽略当前消息；提前返回。
2.1.2消息字符为'xh',则从队列里抽取值到 byte1,byte2中；
2.1.3消息字符为'cs',则byte1,byte2均赋值为51；
赋值：byte3=byteRcv ；发送byte1,byte2,byte3提前返回。
2.2若byteRcv == byte3 则直接发送byte1,byte2,byte3（均是上次保留的值）,不从队列中抽取值；返回。
